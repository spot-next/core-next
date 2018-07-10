package at.spot.core.infrastructure.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.strategy.ImpexImportStrategy;
import at.spot.core.infrastructure.support.impex.ColumnDefinition;
import at.spot.core.infrastructure.support.impex.ImpexCommand;
import at.spot.core.infrastructure.support.impex.WorkUnit;
import at.spot.core.model.Item;
import at.spot.itemtype.core.beans.ImportConfiguration;

@Service
public class DefaultImpexImportStrategy extends AbstractService implements ImpexImportStrategy {

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

	@Override
	public void importImpex(ImportConfiguration config, File file) throws ImpexImportException {
		List<String> fileContent = null;

		try {
			URL resource = getClass().getResource(file.getPath());
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

	protected List<WorkUnit> transformImpex(List<String> fileContent) throws ImpexImportException {
		final List<WorkUnit> workUnits = new ArrayList<>();
		WorkUnit current = null;

		for (String line : fileContent) {

			// ignore empty lines or comments
			if (StringUtils.isBlank(line) || StringUtils.startsWith(line, "#")) {
				continue;
			}

			// looks for commands
			final Optional<ImpexCommand> command = Stream.of(ImpexCommand.values())
					.filter(c -> StringUtils.startsWith(line, c.toString())).findFirst();

			if (command.isPresent()) {
				// start of a new import workunit

				current = new WorkUnit();
				current.setCommand(command.get());
				current.addRawScriptRow(line);

				try {
					current.setItemType(getItemType(line));
				} catch (UnknownTypeException e) {
					throw new ImpexImportException(String.format("Cannot process ImpEx header: %s", line));
				}

				workUnits.add(current);
			} else {
				current.addRawScriptRow(line);
			}
		}

		return workUnits;
	}

	protected Class<? extends Item> getItemType(String line) throws UnknownTypeException {
		String typeName = StringUtils.substring(line, StringUtils.indexOf(line, " "), StringUtils.indexOf(line, ";"))
				.trim().toLowerCase();

		return typeService.getClassForTypeCode(typeName);
	}

	protected void processWorkUnits(List<WorkUnit> units) throws ImpexImportException {
		for (WorkUnit unit : units) {
			try {
				final List<List<String>> parsedRows = parseRawRows(StringUtils.join(unit.getRawScriptRows(), "\n"));

				unit.setHeaderColumns(parseHeaderColumns(parsedRows.get(0)));
				unit.setDataRows(parsedRows.subList(1, parsedRows.size()));
			} catch (IOException e) {
				throw new ImpexImportException(String.format("Cannot process work unit '%s' for type %s",
						unit.getCommand(), unit.getItemType()), e);
			}
		}
	}

	protected List<List<String>> parseRawRows(String lines) throws IOException {
		try (CSVReader reader = new CSVReader(new StringReader(lines), ';')) {
			List<List<String>> rows = new ArrayList<>();

			reader.forEach(row -> {
				rows.add(Stream.of(row).map(c -> c.trim()).collect(Collectors.toList()));
			});

			return rows;
		}
	}

	protected void importWorkUnits(List<WorkUnit> workUnits) throws ImpexImportException {
		for (WorkUnit unit : workUnits) {
			try {
				Item item = modelService.create(unit.getItemType());

				for (List<String> row : unit.getDataRows()) {
					for (int x = 0; x < unit.getHeaderColumns().size(); x++) {

						// ignore first column/ row value as it is always empty
						if (x == 0) {
							continue;
						}

						ColumnDefinition col = unit.getHeaderColumns().get(x);
						String val = row.get(x);

						Object propertyValue = resolveValue(val, col);

						modelService.setPropertyValue(item, col.getPropertyName(), val);
					}
				}

				modelService.save(item);
			} catch (Exception e) {
				throw new ImpexImportException(
						String.format("Could not import item of type %s", unit.getItemType().getName()), e);
			}
		}
	}

	private Object resolveValue(String val, ColumnDefinition col) {
		if (StringUtils.isNotBlank(col.getValueResolutionDescriptor())) {

			// TreeMap<String, V>

			return null;
		} else {
			return val;
		}
	}

	protected List<ColumnDefinition> parseHeaderColumns(List<String> columns) {
		List<ColumnDefinition> parsedColumns = new ArrayList<>();

		boolean isFirst = true;

		for (String col : columns) {
			if (isFirst) {
				isFirst = false;
				continue;
			}

			if (StringUtils.isBlank(col)) {
				continue;
			}

			ColumnDefinition colDef = new ColumnDefinition();

			Matcher m = PATTERN_COLUMN_DEFINITION.matcher(col);

			if (m.matches()) {
				String propertyName = m.group(1);
				String valueResolutionDescriptor = m.group(2);
				String modifiers = m.group(3);

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

	protected Map<String, String> parseModifiers(String modifiers) {
		Map<String, String> parsedModifiers = new HashMap<>();

		String[] kvPairs = StringUtils.removeAll(modifiers, "[\\[\\]]").split(",");

		if (kvPairs.length > 0) {
			Stream.of(kvPairs).forEach(kv -> {
				String[] kvSplit = StringUtils.split(kv, '=');

				if (kvSplit.length == 2) {
					parsedModifiers.put(kvSplit[0], kvSplit[1]);
				}
			});
		}

		return parsedModifiers;
	}
}
