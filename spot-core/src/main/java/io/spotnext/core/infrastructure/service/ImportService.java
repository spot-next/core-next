package io.spotnext.core.infrastructure.service;

import java.io.InputStream;

import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.enumeration.ImportFormat;

public interface ImportService {
	/**
	 * Imports the item models of the given file based on the chosen file
	 * format.<br />
	 * 
	 * @param inputStream that points to the import script. It will be closed after
	 *                    usage!
	 */
	void importItems(ImportFormat format, ImportConfiguration config, InputStream inputStream) throws ImportException;

}
