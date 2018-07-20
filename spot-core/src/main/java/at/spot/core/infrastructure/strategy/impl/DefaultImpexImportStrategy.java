package at.spot.core.infrastructure.strategy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;

import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.ModelQuery;

import at.spot.core.infrastructure.exception.ImpexImportException;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.resolver.impex.ImpexValueResolver;
import at.spot.core.infrastructure.resolver.impex.impl.PrimitiveValueResolver;
import at.spot.core.infrastructure.resolver.impex.impl.ReferenceValueResolver;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.strategy.ImpexImportStrategy;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.LogLevel;
import at.spot.core.infrastructure.support.impex.ColumnDefinition;
import at.spot.core.infrastructure.support.impex.ImpexCommand;
import at.spot.core.infrastructure.support.impex.ImpexMergeMode;
import at.spot.core.infrastructure.support.impex.WorkUnit;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.support.util.ValidationUtil;
import at.spot.core.types.Item;
import at.spot.itemtype.core.beans.ImportConfiguration;

@Service
public class DefaultImpexImportStrategy extends AbstractService implements ImpexImportStrategy {

	public static final String MAP_ENTRY_SEPARATOR = "->";
	public static final String COLLECTION_VALUE_SEPARATOR = ",";

	// (^INSERT_UPDATE|^UPDATE|^INSERT|^UPSERT|^REMOVE)[\s]{0,}([a-zA-Z]{2,})[\s]{0,}(\[.*?\]){0,1}[\s]{0,}[;]{0,1}
	protected Pattern PATTERN_COMMAND_AND_TYPE = Pattern.compile(
			"(^INSERT_UPDATE|^UPDATE|^INSERT|^UPSERT|^REMOVE)[\\s]{0,}([a-zA-Z]{2,})[\\s]{0,}(\\[.*?\\]){0,1}[\\s]{0,}[;]{0,1}");

	// ^[\s]{0,}([a-zA-Z0-9]{2,})(\({0,1}[a-zA-Z0-9,\(\)]{0,}\){0,1})(\[{0,1}[a-zA-Z0-9,\=]{0,}\]{0,1})
	protected Pattern PATTERN_COLUMN_DEFINITION = Pattern.compile(
			"^[\\s]{0,}([a-zA-Z0-9]{2,})(\\({0,1}[a-zA-Z0-9,\\(\\)]{0,}\\){0,1})(\\[{0,1}[a-zA-Z0-9,\\=]{0,}\\]{0,1})");

	@Resource
	private TypeService typeService;

	@Resource
	private ModelService modelService;

	@Resource
	private QueryService queryService;

	@Resource
	private PrimitiveValueResolver primitiveValueResolver;

	@Resource
	private ReferenceValueResolver referenceValueResolver;

	@Resource
	private Map<String, ImpexValueResolver> impexValueResolvers;

	@Override
	public void importImpex(final ImportConfiguration config, final InputStream inputStream)
			throws ImpexImportException {

		ValidationUtil.validateNotNull("Import config cannot be null", config);
		ValidationUtil.validateNotNull("Script input stream cannot be null", inputStream);

		List<String> fileContent = null;

		try {
			fileContent = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ImpexImportException(String.format("Could not read impex file %s", config.getScriptIdentifier()),
					e);
		}

		if (CollectionUtils.isNotEmpty(fileContent)) {
			loggingService.debug(String.format("Transforming %s to work units", config.getScriptIdentifier()));
			final List<WorkUnit> workUnits = transformImpex(fileContent);

			loggingService.debug(String.format("Processing %s work units", workUnits.size()));
			processWorkUnits(workUnits);

			loggingService.debug(String.format("Importing %s work units", workUnits.size()));
			importWorkUnits(workUnits, config);

		} else {
			loggingService.warn(String.format("Ignoring empty file %s", config.getScriptIdentifier()));
		}
	}

	protected List<WorkUnit> transformImpex(final List<String> fileContent) throws ImpexImportException {
		final List<WorkUnit> workUnits = new ArrayList<>();
		WorkUnit current = null;

		for (final String line : fileContent) {

			// there might be some invisible unicode characters that might cause troubles
			// furthermore all spaces
			final String trimmedLine = StringUtils.trim(line);

			// ignore empty lines or comments
			if (StringUtils.isBlank(trimmedLine) || StringUtils.startsWith(trimmedLine, "#")) {
				continue;
			}

			// looks for commands
			final Optional<ImpexCommand> command = Stream.of(ImpexCommand.values())
					.filter(c -> StringUtils.startsWithIgnoreCase(trimmedLine, c.toString())).findFirst();

			if (command.isPresent()) {
				// start of a new import workunit

				current = new WorkUnit();
				current.setCommand(command.get());
				current.addRawScriptRow(trimmedLine);

				try {
					current.setItemType(getItemType(trimmedLine));
				} catch (final UnknownTypeException e) {
					throw new ImpexImportException(String.format("Cannot process ImpEx header: %s", trimmedLine));
				}

				workUnits.add(current);
			} else {
				current.addRawScriptRow(trimmedLine);
			}
		}

		return workUnits;
	}

	protected Class<? extends Item> getItemType(final String line) throws UnknownTypeException {
		final String typeName = StringUtils
				.substring(line, StringUtils.indexOf(line, " "), StringUtils.indexOf(line, ";")).trim().toLowerCase();

		return typeService.getClassForTypeCode(typeName);
	}

	protected void processWorkUnits(final List<WorkUnit> units) throws ImpexImportException {
		for (final WorkUnit unit : units) {
			try {
				final List<List<String>> parsedRows = parseRawRows(StringUtils.join(unit.getRawScriptRows(), "\n"));

				unit.setHeaderColumns(parseHeaderColumns(parsedRows.get(0)));
				unit.setDataRows(parsedRows.subList(1, parsedRows.size()));
			} catch (final IOException e) {
				throw new ImpexImportException(String.format("Cannot process work unit '%s' for type %s",
						unit.getCommand(), unit.getItemType()), e);
			}
		}
	}

	protected List<List<String>> parseRawRows(final String lines) throws IOException {
		try (CSVReader reader = new CSVReader(new StringReader(lines), ';')) {
			final List<List<String>> rows = new ArrayList<>();

			reader.forEach(row -> {
				rows.add(Stream.of(row).map(c -> c.trim()).collect(Collectors.toList()));
			});

			return rows;
		}
	}

	protected void importWorkUnits(final List<WorkUnit> workUnits, ImportConfiguration config)
			throws ImpexImportException {
		final List<Item> itemsToSave = new ArrayList<>();
		final List<JpqlQuery<Void>> itemsToUpdate = new ArrayList<>();
		final List<JpqlQuery<Void>> itemsToRemove = new ArrayList<>();

		for (final WorkUnit unit : workUnits) {
			try {
				// holds a list all raw resolved property values for each row
				final List<Map<ColumnDefinition, Object>> resolvedRawItemsToSave = new ArrayList<>();

				// resolve and collect all the property values
				for (final List<String> row : unit.getDataRows()) {
					final Map<ColumnDefinition, Object> resolvedPropertyValues = new HashMap<>();
					resolvedRawItemsToSave.add(resolvedPropertyValues);

					final String typeCode = typeService.getTypeCodeForClass(unit.getItemType());

					// ignore first column/ row value as it is always empty
					for (int x = 1; x < row.size(); x++) {
						final int headerIndex = x - 1;
						if (headerIndex >= unit.getHeaderColumns().size()) {
							break;
						}

						final ColumnDefinition col = unit.getHeaderColumns().get(headerIndex);
						final String val = row.get(x);

						final ItemTypePropertyDefinition propDef = typeService.getItemTypeProperties(typeCode)
								.get(col.getPropertyName());

						if (propDef != null) {
							final Class<?> returnType = propDef.getReturnType();
							col.setColumnType(returnType);

							// handle collections and maps
							final Object propertyValue = resolveValue(val, returnType,
									propDef.getGenericTypeArguments(), col);

							resolvedPropertyValues.put(col, propertyValue);
						} else {
							loggingService.warn(String.format("Ignoring unknown column %s for type %s",
									col.getPropertyName(), unit.getItemType().getSimpleName()));
						}
					}
				}

				for (Map<ColumnDefinition, Object> rawItem : resolvedRawItemsToSave) {
					if (ImpexCommand.INSERT.equals(unit.getCommand())) {
						// there is no JPA INSERT command, so we just create a new item and save it the
						// regular way
						itemsToSave.add(insertItem(unit, rawItem));

					} else if (ImpexCommand.UPDATE.equals(unit.getCommand())) {
						// First we fetch the item based on the unique columns
						final Map<String, Object> uniqueParams = getUniqueAttributValues(unit.getHeaderColumns(),
								rawItem, config);
						final Item existingItem = modelService.get(new ModelQuery<>(unit.getItemType(), uniqueParams));

						// if no matching item is found, we tread this the same way as an INSERT COMMAND
						if (existingItem != null) {
							itemsToUpdate.add(createUpdateQuery(existingItem, rawItem, uniqueParams.keySet()));
						} else {
							throw new ImpexImportException(String
									.format("Could not find item for update with unique properties: %s", uniqueParams));
						}

					} else if (ImpexCommand.INSERT_UPDATE.equals(unit.getCommand())) {
						// for UDPATE we can create a JPQL query

						// First we fetch the item based on the unique columns
						final Map<String, Object> uniqueParams = getUniqueAttributValues(unit.getHeaderColumns(),
								rawItem, config);
						final Item existingItem = modelService.get(new ModelQuery<>(unit.getItemType(), uniqueParams));

						// if a matching item is found, we update it
						if (existingItem != null) {
							itemsToUpdate.add(createUpdateQuery(existingItem, rawItem, uniqueParams.keySet()));
						} else {
							// otherwise we create an INSERT JPQL query
							itemsToSave.add(insertItem(unit, rawItem));
						}

					} else if (ImpexCommand.REMOVE.equals(unit.getCommand())) {
						itemsToRemove.add(createRemoveQuery(unit, rawItem));
					}
				}
			} catch (final Exception e) {
				throw new ImpexImportException(
						String.format("Could not import item of type %s", unit.getItemType().getName()), e);
			}

			// save each workunit so following units can reference items from before
			loggingService.debug(() -> String.format("Saving %s new items", itemsToSave.size()));

			if (config.getIgnoreErrors()) {
				executeWithIgnoreErrors(itemsToSave, (i) -> modelService.save(i),
						(i, e) -> loggingService.log(LogLevel.WARN, "Could not save item %s - ignoring", null, i));
			} else {
				modelService.saveAll(itemsToSave);
			}

			itemsToSave.clear();

			loggingService.debug(() -> String.format("Removing %s items", itemsToRemove.size()));
			for (JpqlQuery<Void> q : itemsToRemove) {
				queryService.query(q);
			}

			loggingService.debug(() -> String.format("Updating %s items", itemsToUpdate.size()));
			for (JpqlQuery<Void> q : itemsToUpdate) {
				queryService.query(q);
			}
		}
	}

	private <T, E extends Exception> void executeWithIgnoreErrors(Collection<T> objects, Consumer<T> consumer,
			BiConsumer<T, E> exceptionHandler) {

		loggingService.debug(
				() -> "Import config is set to 'ignoreErrors' - this might negativly influence persistence operations!");

		for (T o : objects) {
			try {
				consumer.accept(o);
			} catch (Exception e) {
				exceptionHandler.accept(o, (E) e);
			}
		}
	}

	private Item insertItem(WorkUnit unit, Map<ColumnDefinition, Object> rawItem) {
		final Item item = modelService.create(unit.getItemType());

		for (Map.Entry<ColumnDefinition, Object> property : rawItem.entrySet()) {
			setItemPropertyValue(item, property.getKey(), property.getValue());
		}

		return item;
	}

	private JpqlQuery<Void> createUpdateQuery(Item item, Map<ColumnDefinition, Object> rawItem,
			Set<String> propertyToIgnore) {

		final List<String> whereClauses = new ArrayList<>();
		final Map<String, Object> params = new HashMap<>();
		final String typeName = item.getClass().getSimpleName();

		for (Map.Entry<ColumnDefinition, Object> i : rawItem.entrySet()) {
			// only add the column value if it is not to ignore
			if (propertyToIgnore != null && !propertyToIgnore.contains(i.getKey().getPropertyName())) {
				whereClauses.add(typeName + "." + i.getKey().getPropertyName() + " = :" + i.getKey().getPropertyName());
				params.put(i.getKey().getPropertyName(), i.getValue());
			}
		}

		final String whereClause = whereClauses.stream().collect(Collectors.joining(" AND "));

		params.put("pk", item.getPk());

		JpqlQuery<Void> query = new JpqlQuery<>(
				String.format("UPDATE %s AS %s SET %s WHERE %s.pk = :pk", typeName, typeName, whereClause, typeName),
				params, Void.class);

		return query;
	}

	private JpqlQuery<Void> createRemoveQuery(WorkUnit unit, Map<ColumnDefinition, Object> rawItem) {
		final List<String> whereClauses = new ArrayList<>();
		final Map<String, Object> params = new HashMap<>();
		final String typeName = unit.getItemType().getSimpleName();

		for (Map.Entry<ColumnDefinition, Object> i : rawItem.entrySet()) {
			whereClauses.add(typeName + "." + i.getKey().getPropertyName() + " = :" + i.getKey().getPropertyName());
			params.put(i.getKey().getPropertyName(), i.getValue());
		}

		final String whereClause = whereClauses.stream().collect(Collectors.joining(" AND "));

		JpqlQuery<Void> query = new JpqlQuery<>(
				String.format("DELETE FROM %s AS %s WHERE %s", typeName, typeName, whereClause), params, Void.class);

		return query;
	}

	/**
	 * Return the unique resolved (!) properties.
	 * 
	 * @throws ImpexImportException
	 *             if one of the given columns is a collection or a map, as this is
	 *             not supported.
	 */
	private Map<String, Object> getUniqueAttributValues(List<ColumnDefinition> headerColumns,
			Map<ColumnDefinition, Object> rawItem, ImportConfiguration config) throws ImpexImportException {

		final Map<String, Object> ret = new HashMap<>();

		for (ColumnDefinition col : headerColumns) {
			if (isCollectionType(col) || isMapType(col)) {
				final String message = "Columns with type Collection or Map cannot be used as unique identifiers.";

				if (config.getIgnoreErrors()) {
					loggingService.warn(message);
				} else {
					throw new ImpexImportException(message);
				}
			}

			// only look at the unique properties
			if (col.getModifiers().containsKey("unique")) {
				ret.put(col.getPropertyName(), rawItem.get(col));
			}
		}

		return ret;
	}

	private boolean isCollectionType(ColumnDefinition columnDefinition) {
		return Collection.class.isAssignableFrom(columnDefinition.getColumnType());
	}

	private boolean isMapType(ColumnDefinition columnDefinition) {
		return Map.class.isAssignableFrom(columnDefinition.getColumnType());
	}

	@SuppressWarnings("unchecked")
	private void setItemPropertyValue(Item item, ColumnDefinition columnDefinition, Object propertyValue) {

		if (isCollectionType(columnDefinition)) {
			Collection<?> colValue = (Collection<?>) modelService.getPropertyValue(item,
					columnDefinition.getPropertyName());

			switch (getMergeMode(columnDefinition)) {
			case APPEND:
			case ADD:
				if (colValue == null) {
					modelService.setPropertyValue(item, columnDefinition.getPropertyName(), propertyValue);
				}
				colValue.addAll((Collection) propertyValue);
				break;
			case REMOVE:
				if (colValue != null) {
					colValue.removeAll((Collection<?>) propertyValue);
				}
				break;
			case REPLACE:
				colValue = (Collection<?>) propertyValue;
				break;
			}

		} else if (isMapType(columnDefinition)) {
			Map<Object, Object> mapValue = (Map<Object, Object>) modelService.getPropertyValue(item,
					columnDefinition.getPropertyName());

			switch (getMergeMode(columnDefinition)) {
			case APPEND:
			case ADD:
				if (mapValue == null) {
					modelService.setPropertyValue(item, columnDefinition.getPropertyName(), propertyValue);
				}
				mapValue.putAll((Map<?, ?>) propertyValue);
				break;
			case REMOVE:
				if (mapValue != null) {
					for (Object key : ((Map<?, ?>) propertyValue).keySet()) {
						mapValue.remove(key);
					}
				}
				break;
			case REPLACE:
				mapValue = (Map) propertyValue;
				break;
			}
		} else {
			modelService.setPropertyValue(item, columnDefinition.getPropertyName(), propertyValue);
		}
	}

	private ImpexMergeMode getMergeMode(ColumnDefinition columnDefinition) {
		final String modeVal = columnDefinition.getModifiers().get("mode");
		ImpexMergeMode mode = ImpexMergeMode.ADD;

		if (modeVal != null) {
			mode = ImpexMergeMode.valueOf(modeVal.toUpperCase());
		}

		return mode;
	}

	private Object resolveValue(final String value, final Class<?> type, final List<Class<?>> genericArguments,
			final ColumnDefinition columnDefinition) {

		Object ret = null;

		if (Collection.class.isAssignableFrom(type)) {
			final String[] collectionValues = value.split(COLLECTION_VALUE_SEPARATOR);
			final List<Object> resolvedValues = new ArrayList<>();
			ret = resolvedValues;

			for (final String v : collectionValues) {
				resolvedValues.add(resolveSingleValue(v, genericArguments.get(0), columnDefinition));
			}
		} else if (Map.class.isAssignableFrom(type)) {
			final String[] mapEntryValues = value.split(COLLECTION_VALUE_SEPARATOR);
			final Map<Object, Object> resolvedValues = new HashMap<>();
			ret = resolvedValues;

			// resolve both key and value of the map
			for (final String v : mapEntryValues) {
				final String[] splitEntry = StringUtils.split(v, MAP_ENTRY_SEPARATOR);
				final Object entryKey = resolveSingleValue(splitEntry[0], genericArguments.get(0), columnDefinition);
				final Object entryValue = resolveSingleValue(splitEntry[0], genericArguments.get(1), columnDefinition);
				resolvedValues.put(entryKey, entryValue);
			}
		} else {
			ret = resolveSingleValue(value, type, columnDefinition);
		}

		return ret;
	}

	private Object resolveSingleValue(final String value, final Class<?> type,
			final ColumnDefinition columnDefinition) {

		if (StringUtils.isNotBlank(columnDefinition.getValueResolutionDescriptor())) {
			return referenceValueResolver.resolve(value, type, null, columnDefinition);
		} else {
			return primitiveValueResolver.resolve(value, type, null, columnDefinition);
		}
	}

	protected List<ColumnDefinition> parseHeaderColumns(final List<String> columns) {
		final List<ColumnDefinition> parsedColumns = new ArrayList<>();

		boolean isFirst = true;

		for (final String col : columns) {
			if (isFirst) {
				isFirst = false;
				continue;
			}

			if (StringUtils.isBlank(col)) {
				continue;
			}

			final ColumnDefinition colDef = new ColumnDefinition();

			final Matcher m = PATTERN_COLUMN_DEFINITION.matcher(col);

			if (m.matches()) {
				final String propertyName = m.group(1);
				final String valueResolutionDescriptor = m.group(2);
				final String modifiers = m.group(3);

				colDef.setPropertyName(propertyName);
				colDef.setValueResolutionDescriptor(valueResolutionDescriptor);
				colDef.addModifiers(parseModifiers(modifiers));

				parsedColumns.add(colDef);
			} else {
				loggingService.warn(String.format("Could not parse header column: %s", col));
			}
		}

		return parsedColumns;
	}

	protected Map<String, String> parseModifiers(final String modifiers) {

		if (StringUtils.isNotBlank(modifiers)) {
			final Map<String, String> parsedModifiers = new HashMap<>();

			final String[] kvPairs = StringUtils.removeAll(modifiers, "[\\[\\]]").split(COLLECTION_VALUE_SEPARATOR);

			if (kvPairs.length > 0) {
				Stream.of(kvPairs).forEach(kv -> {
					final String[] kvSplit = StringUtils.split(kv, '=');

					if (kvSplit.length == 2) {
						parsedModifiers.put(kvSplit[0], kvSplit[1]);
					}
				});
			}

			return parsedModifiers;
		}

		return Collections.emptyMap();
	}
}
