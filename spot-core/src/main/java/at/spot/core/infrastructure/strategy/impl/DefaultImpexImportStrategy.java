package at.spot.core.infrastructure.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;

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
import at.spot.core.infrastructure.support.impex.ColumnDefinition;
import at.spot.core.infrastructure.support.impex.ImpexCommand;
import at.spot.core.infrastructure.support.impex.ImpexMergeMode;
import at.spot.core.infrastructure.support.impex.WorkUnit;
import at.spot.core.model.Item;
import at.spot.itemtype.core.beans.ImportConfiguration;

@Service
public class DefaultImpexImportStrategy extends AbstractService implements ImpexImportStrategy {

	public static final String MAP_ENTRY_SEPARATOR = "->";

	public static final String COLLECTION_VALUE_SEPARATOR = ",";

	// (^INSERT_UPDATE|^UPDATE|^INSERT|^UPSERT|^REMOVE)[\s]{0,}([a-zA-Z]{2,})[\s]{0,}(\[.*?\]){0,1}[\s]{0,}[;]{0,1}
	protected Pattern PATTERN_COMMAND_AND_TYPE = Pattern.compile(
			"(^INSERT_UPDATE|^UPDATE|^INSERT|^UPSERT|^REMOVE)[\\s]{0,}([a-zA-Z]{2,})[\\s]{0,}(\\[.*?\\]){0,1}[\\s]{0,}[;]{0,1}");

	// ^[\s]{0,}([a-zA-Z0-9]{2,})(\(.*?\)){0,1}(\[.*?\]){0,1}.*$
	protected Pattern PATTERN_COLUMN_DEFINITION = Pattern
			.compile("^[\\s]{0,}([a-zA-Z0-9]{2,})(\\(.*?\\)){0,1}(\\[.*?\\]){0,1}.*$");

	@Resource
	private TypeService typeService;

	@Resource
	private ModelService modelService;

	@Resource
	private PrimitiveValueResolver primitiveValueResolver;

	@Resource
	private ReferenceValueResolver referenceValueResolver;

	@Resource
	private Map<String, ImpexValueResolver> impexValueResolvers;

	@Override
	public void importImpex(final ImportConfiguration config, final File file) throws ImpexImportException {
		List<String> fileContent = null;

		try {
			final URL resource = getClass().getResource(file.getPath());
			fileContent = Files.readAllLines(Paths.get(resource.toURI()));
		} catch (IOException | URISyntaxException e) {
			throw new ImpexImportException("Could not read impex file", e);
		}

		if (CollectionUtils.isNotEmpty(fileContent)) {
			loggingService.debug(String.format("Transforming %s to work units", file.getPath()));
			final List<WorkUnit> workUnits = transformImpex(fileContent);

			loggingService.debug(String.format("Processing %s work units", workUnits.size()));
			processWorkUnits(workUnits);

			loggingService.debug(String.format("Importing %s work units", workUnits.size()));
			importWorkUnits(workUnits);

		} else {
			loggingService.warn(String.format("Ignoring empty file %s", file.toString()));
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

	protected void importWorkUnits(final List<WorkUnit> workUnits) throws ImpexImportException {
		List<Item> itemsToSave = new ArrayList<>();

		for (final WorkUnit unit : workUnits) {
			try {
				for (final List<String> row : unit.getDataRows()) {

					final Item item = modelService.create(unit.getItemType());
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

						final Class<?> returnType = propDef.getReturnType();

						// handle collections and maps
						final Object propertyValue = resolveValue(val, returnType, propDef.getGenericTypeArguments(),
								col);

						setValue(item, propDef, col, propertyValue);
					}

					itemsToSave.add(item);
				}

			} catch (final Exception e) {
				throw new ImpexImportException(
						String.format("Could not import item of type %s", unit.getItemType().getName()), e);
			}
		}

		modelService.saveAll(itemsToSave);
	}

	@SuppressWarnings("unchecked")
	private void setValue(Item item, ItemTypePropertyDefinition property, ColumnDefinition columnDefinition,
			Object propertyValue) {

		if (Collection.class.isAssignableFrom(property.getReturnType())) {
			Collection<? extends Object> colValue = (Collection<? extends Object>) modelService.getPropertyValue(item,
					property.getName());

			switch (getMergeMode(columnDefinition)) {
			case ADD:
				if (colValue == null) {
					modelService.setPropertyValue(item, property.getName(), propertyValue);
				}
				colValue.addAll((Collection) propertyValue);
				break;
			case REMOVE:
				if (colValue != null) {
					colValue.removeAll((Collection) propertyValue);
				}
				break;
			case REPLACE:
				colValue = (Collection) propertyValue;
				break;
			}

		} else if (Map.class.isAssignableFrom(property.getReturnType())) {
			Map<Object, Object> mapValue = (Map<Object, Object>) modelService.getPropertyValue(item,
					property.getName());

			switch (getMergeMode(columnDefinition)) {
			case ADD:
				if (mapValue == null) {
					modelService.setPropertyValue(item, property.getName(), propertyValue);
				}
				mapValue.putAll((Map) propertyValue);
				break;
			case REMOVE:
				if (mapValue != null) {
					for (Object key : ((Map) propertyValue).keySet()) {
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
			mode = ImpexMergeMode.valueOf(modeVal);
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
