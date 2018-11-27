package io.spotnext.core.infrastructure.strategy.impl;

import static io.spotnext.support.util.MiscUtil.$;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.ImpexImportException;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.resolver.impex.ImpexValueResolver;
import io.spotnext.core.infrastructure.resolver.impex.impl.PrimitiveValueResolver;
import io.spotnext.core.infrastructure.resolver.impex.impl.ReferenceValueResolver;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.strategy.ImpexImportStrategy;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;
import io.spotnext.core.infrastructure.support.impex.ImpexCommand;
import io.spotnext.core.infrastructure.support.impex.ImpexMergeMode;
import io.spotnext.core.infrastructure.support.impex.WorkUnit;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.persistence.service.TransactionService;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.support.util.MiscUtil;
import io.spotnext.support.util.ValidationUtil;

/**
 * <p>
 * DefaultImpexImportStrategy class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultImpexImportStrategy extends AbstractService implements ImpexImportStrategy {

	/** Constant <code>MAP_ENTRY_SEPARATOR="->"</code> */
	public static final String MAP_ENTRY_SEPARATOR = "->";
	/** Constant <code>COLLECTION_VALUE_SEPARATOR=","</code> */
	public static final String COLLECTION_VALUE_SEPARATOR = ",";

	// ^[\s]{0,}([a-zA-Z0-9]{2,})(\({0,1}[a-zA-Z0-9,\(\)]{0,}\){0,1})(\[{0,1}[a-zA-Z0-9,._\-\=]{0,}\]{0,1})
	protected Pattern PATTERN_COLUMN_DEFINITION = Pattern
			.compile("^[\\s]{0,}([a-zA-Z0-9]{2,})(\\({0,1}[a-zA-Z0-9,\\(\\)]{0,}\\){0,1})(\\[{0,1}[a-zA-Z0-9,._\\-\\=]{0,}\\]{0,1})");

	@Autowired
	private TypeService typeService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private QueryService queryService;

	@Autowired
	private PrimitiveValueResolver primitiveValueResolver;

	@Autowired
	private ReferenceValueResolver referenceValueResolver;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private Map<String, ImpexValueResolver> impexValueResolvers;

	/** {@inheritDoc} */
	@Override
	public void importImpex(final ImportConfiguration config, final InputStream inputStream) throws ImpexImportException {

		ValidationUtil.validateNotNull("Import config cannot be null", config);
		ValidationUtil.validateNotNull(String.format("Script input stream cannot be null (identifier='%s')", config.getScriptIdentifier()), inputStream);

		List<String> fileContent = null;

		try {
			fileContent = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			throw new ImpexImportException(String.format("Could not read impex file %s", config.getScriptIdentifier()), e);
		}

		if (CollectionUtils.isNotEmpty(fileContent)) {
			Logger.debug(String.format("Transforming %s to work units", config.getScriptIdentifier()));
			final List<WorkUnit> workUnits = transformImpex(fileContent);

			Logger.debug(String.format("Processing %s work units", workUnits.size()));
			processWorkUnits(workUnits);

			Logger.debug(String.format("Importing %s work units", workUnits.size()));
			transactionService.executeWithoutResult(() -> importWorkUnits(workUnits, config));
		} else {
			Logger.warn(String.format("Ignoring empty file %s", config.getScriptIdentifier()));
		}
	}

	protected List<WorkUnit> transformImpex(final List<String> fileContent) throws ImpexImportException {
		final List<WorkUnit> workUnits = new ArrayList<>();
		WorkUnit current = null;

		for (final String line : fileContent) {

			// there might be some invisible unicode characters that might cause
			// troubles
			// furthermore all spaces
			final String trimmedLine = StringUtils.trim(line);

			// ignore empty lines or comments
			if (StringUtils.isBlank(trimmedLine) || StringUtils.startsWith(trimmedLine, "#")) {
				continue;
			}

			// looks for commands
			final String stringCommand = StringUtils.trimToEmpty(trimmedLine.substring(0, trimmedLine.indexOf(" ")));
			final Optional<ImpexCommand> command = parseImpexCommand(stringCommand);

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

	private Optional<ImpexCommand> parseImpexCommand(String stringCommand) {
		ImpexCommand command = null;
		try {
			command = ImpexCommand.valueOf(stringCommand.toUpperCase(Locale.getDefault()));
		} catch (IllegalArgumentException e) {
			// ignore not found
		}

		return Optional.ofNullable(command);
	}

	protected Class<? extends Item> getItemType(final String line) throws UnknownTypeException {
		String typeName = StringUtils.substring(line, StringUtils.indexOf(line, " "), StringUtils.indexOf(line, ";"));
		typeName = StringUtils.lowerCase(StringUtils.trim(typeName));

		return typeService.getClassForTypeCode(typeName);
	}

	protected void processWorkUnits(final List<WorkUnit> units) throws ImpexImportException {
		for (final WorkUnit unit : units) {
			try {
				final List<List<String>> parsedRows = parseRawRows(StringUtils.join(unit.getRawScriptRows(), "\n"));

				unit.setHeaderColumns(parseHeaderColumns(parsedRows.get(0)));
				unit.setDataRows(parsedRows.subList(1, parsedRows.size()));
			} catch (final IOException e) {
				throw new ImpexImportException(String.format("Cannot process work unit '%s' for type %s", unit.getCommand(), unit.getItemType()), e);
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

	protected void importWorkUnits(final List<WorkUnit> workUnits, final ImportConfiguration config) throws ImpexImportException {

		final List<JpqlQuery<Void>> itemsToRemove = new ArrayList<>();

		for (final WorkUnit unit : workUnits) {
			List<String> currentRow = null;

			try {
				// resolve and collect all the property values
				for (final List<String> row : unit.getDataRows()) {
					currentRow = row;

					final Map<ColumnDefinition, Object> rawItem = new HashMap<>();

					final String typeCode = typeService.getTypeCodeForClass(unit.getItemType());

					// ignore first column/ row value as it is always empty
					for (int x = 1; x < row.size(); x++) {
						final int headerIndex = x - 1;
						if (headerIndex >= unit.getHeaderColumns().size()) {
							break;
						}

						final ColumnDefinition col = unit.getHeaderColumns().get(headerIndex);
						final String val = row.get(x);

						final ItemTypePropertyDefinition propDef = typeService.getItemTypeProperties(typeCode).get(col.getPropertyName());

						if (propDef != null) {
							final Class<?> returnType = propDef.getReturnType();
							col.setColumnType(returnType);

							// resolve values using the default or the
							// configured value resolvers
							try {
								final Object propertyValue = resolveValue(val, returnType, propDef.getGenericTypeArguments(), col);
								rawItem.put(col, propertyValue);
							} catch (final ValueResolverException e) {
								if (config.getIgnoreErrors()) {
									Logger.warn(String.format("Could not resolve value '%s' for %s.%s", val,
											$(() -> unit.getItemType().getSimpleName(), "<null>"), col.getPropertyName()));
								} else {
									throw e;
								}
							}
						} else {
							Logger.warn(String.format("Ignoring unknown column %s for type %s", col.getPropertyName(), unit.getItemType().getSimpleName()));
						}
					}

					// insert/update/remove items
					if (ImpexCommand.INSERT.equals(unit.getCommand())) {
						saveItem(config, insertItem(unit, rawItem));

					} else if (ImpexCommand.UPDATE.equals(unit.getCommand())) {

						// First we fetch the item based on the unique columns
						final Map<String, Object> uniqueParams = getUniqueAttributValues(unit.getHeaderColumns(), rawItem, config);
						final Item existingItem = modelService.get(new ModelQuery<>(unit.getItemType(), uniqueParams));

						// if no matching item is found, we tread this the same
						// way as an INSERT COMMAND
						if (existingItem != null) {
							setItemValues(existingItem, rawItem);
							saveItem(config, existingItem);
						} else {
							final String message = String.format("Could not find item for update with unique properties: %s", uniqueParams);

							if (config.getIgnoreErrors()) {
								Logger.warn(message);
							} else {
								throw new ImpexImportException(message);
							}
						}

					} else if (ImpexCommand.INSERT_UPDATE.equals(unit.getCommand())) {
						// First we fetch the item based on the unique columns
						final Map<String, Object> uniqueParams = getUniqueAttributValues(unit.getHeaderColumns(), rawItem, config);
						final Item existingItem = modelService.get(new ModelQuery<>(unit.getItemType(), uniqueParams));

						// if a matching item is found, we update it
						if (existingItem != null) {
							setItemValues(existingItem, rawItem);
							saveItem(config, existingItem);
						} else {
							// otherwise we create an new item
							saveItem(config, insertItem(unit, rawItem));
						}

					} else if (ImpexCommand.REMOVE.equals(unit.getCommand())) {
						itemsToRemove.add(createRemoveQuery(unit, rawItem));
					}
				}
			} catch (final Throwable e) {
				final String message = String.format("Could not import item of type %s (%s). Line that caused this error: %s", unit.getItemType().getName(),
						e.getMessage(), StringUtils.join(currentRow, ", "));

				if (!config.getIgnoreErrors()) {
					throw new ImpexImportException(message, e);
				} else {
					Logger.warn(message);
				}
			}

			Logger.debug(() -> String.format("Removing %s items", itemsToRemove.size()));
			for (final JpqlQuery<Void> q : itemsToRemove) {
				queryService.query(q);
			}

			itemsToRemove.clear();
		}
	}

	protected void saveItem(final ImportConfiguration config, final Item item) {
		if (config.getIgnoreErrors()) {
			executeWithIgnoreErrors(Arrays.asList(item), (i) -> modelService.save(i),
					(i, e) -> Logger.log(LogLevel.WARN, String.format("Could not save item %s: %s", i, e.getMessage()), null, item));
		} else {
			modelService.saveAll(Arrays.asList(item));
		}
	}

	private <T, E extends Exception> void executeWithIgnoreErrors(final Collection<T> objects, final Consumer<T> consumer,
			final BiConsumer<T, E> exceptionHandler) {

		Logger.debug(() -> "Import config is set to 'ignoreErrors' - this might negativly influence persistence operations!");

		for (final T o : objects) {
			try {
				consumer.accept(o);
			} catch (final Exception e) {
				exceptionHandler.accept(o, (E) e);
			}
		}
	}

	private Item insertItem(final WorkUnit unit, final Map<ColumnDefinition, Object> rawItem) {
		final Item item = modelService.create(unit.getItemType());

		setItemValues(item, rawItem);

		return item;
	}

	private void setItemValues(final Item item, final Map<ColumnDefinition, Object> rawItem) {
		for (final Map.Entry<ColumnDefinition, Object> property : rawItem.entrySet()) {
			setItemPropertyValue(item, property.getKey(), property.getValue());
		}
	}

	// @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
	private JpqlQuery<Void> createUpdateQuery(final Item item, final Map<ColumnDefinition, Object> rawItem, final Set<String> propertyToIgnore) {

		final List<String> whereClauses = new ArrayList<>();
		final Map<String, Object> params = new HashMap<>();
		final String typeName = item.getClass().getSimpleName();

		for (final Map.Entry<ColumnDefinition, Object> i : rawItem.entrySet()) {
			// only add the column value if it is not to ignore
			if (propertyToIgnore != null && !propertyToIgnore.contains(i.getKey().getPropertyName())) {
				whereClauses.add(typeName + "." + i.getKey().getPropertyName() + " = :" + i.getKey().getPropertyName());
				params.put(i.getKey().getPropertyName(), i.getValue());
			}
		}

		final String whereClause = whereClauses.stream().collect(Collectors.joining(" AND "));

		params.put("pk", item.getPk());

		final JpqlQuery<Void> query = new JpqlQuery<>(String.format("UPDATE %s AS %s SET %s WHERE %s.pk = :pk", typeName, typeName, whereClause, typeName),
				params, Void.class);
		query.setClearCaches(true);

		return query;
	}

	private JpqlQuery<Void> createRemoveQuery(final WorkUnit unit, final Map<ColumnDefinition, Object> rawItem) {
		final List<String> whereClauses = new ArrayList<>();
		final Map<String, Object> params = new HashMap<>();
		final String typeName = unit.getItemType().getSimpleName();

		for (final Map.Entry<ColumnDefinition, Object> i : rawItem.entrySet()) {
			whereClauses.add(typeName + "." + i.getKey().getPropertyName() + " = :" + i.getKey().getPropertyName());
			params.put(i.getKey().getPropertyName(), i.getValue());
		}

		final String whereClause = whereClauses.stream().collect(Collectors.joining(" AND "));

		final JpqlQuery<Void> query = new JpqlQuery<>(String.format("DELETE FROM %s AS %s WHERE %s", typeName, typeName, whereClause), params, Void.class);
		query.setClearCaches(true);

		return query;
	}

	/**
	 * Return the unique resolved (!) properties.
	 * 
	 * @throws ImpexImportException if one of the given columns is a collection or a map, as this is not supported.
	 */
	private Map<String, Object> getUniqueAttributValues(final List<ColumnDefinition> headerColumns, final Map<ColumnDefinition, Object> rawItem,
			final ImportConfiguration config) throws ImpexImportException {

		final Map<String, Object> ret = new HashMap<>();

		for (final ColumnDefinition col : headerColumns) {
			// only look at the unique properties
			if (BooleanUtils.toBoolean((String) col.getModifiers().get("unique"))) {
				if (isCollectionType(col) || isMapType(col)) {
					final String message = "Columns with type Collection or Map cannot be used as unique identifiers.";

					if (config.getIgnoreErrors()) {
						Logger.warn(message);
					} else {
						throw new ImpexImportException(message);
					}
				}

				ret.put(col.getPropertyName(), rawItem.get(col));
			}
		}

		return ret;
	}

	private boolean isCollectionType(final ColumnDefinition columnDefinition) {
		return Collection.class.isAssignableFrom(columnDefinition.getColumnType());
	}

	private boolean isMapType(final ColumnDefinition columnDefinition) {
		return Map.class.isAssignableFrom(columnDefinition.getColumnType());
	}

	@SuppressWarnings("unchecked")
	private void setItemPropertyValue(final Item item, final ColumnDefinition columnDefinition, final Object propertyValue) throws ImpexImportException {

		if (isCollectionType(columnDefinition)) {
			Collection<?> colValue = (Collection<?>) modelService.getPropertyValue(item, columnDefinition.getPropertyName());

			switch (getMergeMode(columnDefinition)) {
			case APPEND:
			case ADD:
				if (colValue == null) {
					colValue = (Collection) propertyValue;
				} else {
					colValue.addAll((Collection) propertyValue);
				}
				break;
			case REMOVE:
				if (colValue != null) {
					colValue.removeAll((Collection<?>) propertyValue);
				}
				break;
			case REPLACE:
				if (colValue == null) {
					throw new ImpexImportException(String.format("Collection property of item type %s was not initialized.", item.getTypeCode()));
				}

				if (propertyValue != null) {
					colValue.addAll((Collection) propertyValue);
				} else {
					colValue.clear();
				}

				break;
			}

			setPropertyValue(item, columnDefinition, colValue);

		} else if (isMapType(columnDefinition)) {
			Map<Object, Object> mapValue = (Map<Object, Object>) modelService.getPropertyValue(item, columnDefinition.getPropertyName());

			switch (getMergeMode(columnDefinition)) {
			case APPEND:
			case ADD:
				if (mapValue == null) {
					mapValue = (Map<Object, Object>) propertyValue;
				} else {
					mapValue.putAll((Map<Object, Object>) propertyValue);
				}
				break;
			case REMOVE:
				if (mapValue != null) {
					for (final Object key : ((Map<?, ?>) propertyValue).keySet()) {
						mapValue.remove(key);
					}
				}
				break;
			case REPLACE:
				if (mapValue == null) {
					throw new ImpexImportException(String.format("Map property of item type %s was not initialized.", item.getTypeCode()));
				}

				if (propertyValue != null) {
					mapValue.putAll((Map) propertyValue);
				} else {
					mapValue.clear();
				}

				break;
			}

			setPropertyValue(item, columnDefinition, mapValue);

		} else {
			setPropertyValue(item, columnDefinition, propertyValue);
		}
	}

	private void setPropertyValue(final Item item, final ColumnDefinition columnDefinition, final Object value) {
		final String localeString = columnDefinition.getModifiers().get("lang");
		Locale locale = null;

		if (StringUtils.isNotBlank(localeString)) {
			try {
				locale = MiscUtil.parseLocale(localeString);
			} catch (final IllegalStateException e) {
				Logger.warn(String.format("Unknown locale %s", localeString));
			}
		}

		if (locale != null) {
			modelService.setLocalizedPropertyValue(item, columnDefinition.getPropertyName(), value, locale);
		} else {
			modelService.setPropertyValue(item, columnDefinition.getPropertyName(), value);
		}
	}

	private ImpexMergeMode getMergeMode(final ColumnDefinition columnDefinition) {
		final String modeVal = columnDefinition.getModifiers().get("mode");
		ImpexMergeMode mode = ImpexMergeMode.REPLACE;

		if (StringUtils.isNotBlank(modeVal)) {
			mode = ImpexMergeMode.forCode(modeVal);
		}

		return mode;
	}

	private Object resolveValue(final String value, final Class<?> type, final List<Class<?>> genericArguments, final ColumnDefinition columnDefinition) {

		Object ret = null;

		// if value is null, check for default value in the column definition
		final String val = StringUtils.isNotBlank(value) ? value : columnDefinition.getModifiers().get("default");

		if (StringUtils.isNotBlank(val)) {
			if (Collection.class.isAssignableFrom(type)) {
				final String[] collectionValues = val.split(COLLECTION_VALUE_SEPARATOR);
				final List<Object> resolvedValues = new ArrayList<>();
				ret = resolvedValues;

				for (final String v : collectionValues) {
					resolvedValues.add(resolveSingleValue(v, genericArguments.get(0), columnDefinition));
				}
			} else if (Map.class.isAssignableFrom(type)) {
				final String[] mapEntryValues = val.split(COLLECTION_VALUE_SEPARATOR);
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
				ret = resolveSingleValue(val, type, columnDefinition);
			}
		} else {
			Logger.debug(() -> String.format("Ignoring empty value and default value for %s.%s", type.getSimpleName(),
					columnDefinition.getPropertyName()));
		}

		return ret;
	}

	private Object resolveSingleValue(final String value, final Class<?> type, final ColumnDefinition columnDefinition) {

		if (StringUtils.isNotBlank(columnDefinition.getValueResolutionDescriptor())) {
			return referenceValueResolver.resolve(value, type, null, columnDefinition);
		} else {
			String resolverClass = columnDefinition.getModifiers().get("resolver");
			if (StringUtils.isNotBlank(resolverClass)) {
				final ImpexValueResolver resolver = impexValueResolvers.get(resolverClass);

				return resolver.resolve(value, type, null, columnDefinition);
			} else {
				return primitiveValueResolver.resolve(value, type, null, columnDefinition);
			}
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

			String trimmedCol = col.replace(" ", "");

			if (StringUtils.isBlank(trimmedCol)) {
				continue;
			}

			final ColumnDefinition colDef = new ColumnDefinition();

			final Matcher m = PATTERN_COLUMN_DEFINITION.matcher(trimmedCol);

			if (m.matches()) {
				final String propertyName = m.group(1);
				final String valueResolutionDescriptor = m.group(2);
				final String modifiers = m.group(3);

				colDef.setPropertyName(propertyName);
				colDef.setValueResolutionDescriptor(valueResolutionDescriptor);
				colDef.addModifiers(parseModifiers(modifiers));

				parsedColumns.add(colDef);
			} else {
				Logger.warn(String.format("Could not parse header column: %s", col));
			}
		}

		return parsedColumns;
	}

	protected Map<String, String> parseModifiers(final String modifiers) {

		if (StringUtils.isNotBlank(modifiers)) {
			final Map<String, String> parsedModifiers = new HashMap<>();

			// remove all spaces
			String trimmedModifiers = modifiers.replace(" ", "");

			final String[] kvPairs = StringUtils.removeAll(trimmedModifiers, "[\\[\\]]").split(COLLECTION_VALUE_SEPARATOR);

			if (kvPairs.length > 0) {
				Stream.of(kvPairs).forEach(kv -> {
					final String[] kvSplit = StringUtils.split(kv, '=');

					if (kvSplit.length == 2) {
						parsedModifiers.put(StringUtils.trim(kvSplit[0]), StringUtils.trim(kvSplit[1]));
					}
				});
			}

			return parsedModifiers;
		}

		return Collections.emptyMap();
	}
}
