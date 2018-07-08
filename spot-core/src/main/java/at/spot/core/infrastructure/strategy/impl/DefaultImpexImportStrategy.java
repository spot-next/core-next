package at.spot.core.infrastructure.strategy.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import at.spot.core.infrastructure.exception.ImpexImportException;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.strategy.ImpexImportStrategy;
import at.spot.itemtype.core.beans.ImportConfiguration;

public class DefaultImpexImportStrategy extends AbstractService implements ImpexImportStrategy {

	private static final String[] COMMANDS = { "INSERT", "UPDATE", "INSERT_UPDATE", "UPSERT", "REMOVE" };

	@Override
	public void importImpex(ImportConfiguration config, File file) throws ImpexImportException {

		List<String> fileContent = null;

		try {
			fileContent = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			throw new ImpexImportException("Could not read impex file", e);
		}

		if (fileContent != null) {
			for (String line : fileContent) {
				Optional<String> command = Stream.of(COMMANDS).filter(c -> StringUtils.startsWith(line, c)).findFirst();

				if (command.isPresent()) {
					switch (command.get()) {
					case "INSERT":

						break;
					case "UPDAATE":

						break;
					case "INSERT_UPDATE":
					case "UPSERT":

						break;
					case "REMOVE":

						break;

					default:
						break;
					}
				}
			}
		} else {
			loggingService.warn(String.format("Ignoring empty file %s", file.toString()));
		}
	}

}
