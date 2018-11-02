package io.spotnext.core.infrastructure.service;

import java.io.InputStream;

import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.itemtype.core.beans.ImportConfiguration;

/**
 * <p>ImportService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ImportService {
	/**
	 * Imports the item models of the given file based on the chosen file
	 * format.<br />
	 *
	 * @param config holds the configuration used to customize the import process
	 * @param inputStream that points to the import script. It will be closed after
	 *                    usage!
	 * @throws io.spotnext.infrastructure.exception.ImportException if any.
	 */
	void importItems(ImportConfiguration config, InputStream inputStream) throws ImportException;

}
