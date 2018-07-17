package at.spot.core.infrastructure.service;

import java.io.InputStream;

import at.spot.core.infrastructure.exception.ImportException;
import at.spot.itemtype.core.beans.ImportConfiguration;
import at.spot.itemtype.core.enumeration.ImportFormat;

public interface ImportService {
	/**
	 * Imports the item models of the given file based on the chosen file format.
	 */
	void importItems(ImportFormat format, ImportConfiguration config, InputStream inputStream) throws ImportException;
}
