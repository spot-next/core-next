package at.spot.core.infrastructure.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ImportException;
import at.spot.core.infrastructure.service.ImportService;
import at.spot.core.infrastructure.strategy.ImpexImportStrategy;
import at.spot.itemtype.core.beans.ImportConfiguration;
import at.spot.itemtype.core.enumeration.ImportFormat;

@Service
public class DefaultImportService implements ImportService {

	@Resource
	protected ImpexImportStrategy impexImportStrategy;

	@Override
	public void importItems(ImportFormat format, ImportConfiguration config, File file) throws ImportException {
		if (ImportFormat.ImpEx.equals(format)) {
			try {
				impexImportStrategy.importImpex(config, new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new ImportException(String.format("Cannot read file %s", file.toString()), e);
			}
		} else {
			throw new ImportException(String.format("Format %s is currently not implemented", format));
		}
	}

}
