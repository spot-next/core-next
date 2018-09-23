package io.spotnext.core.infrastructure.service;

import java.io.InputStream;

import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.enumeration.ImportFormat;

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
	 * @param inputStream that points to the import script. It will be closed after
	 *                    usage!
	 * @param format a {@link io.spotnext.itemtype.core.enumeration.ImportFormat} object.
	 * @param config a {@link io.spotnext.itemtype.core.beans.ImportConfiguration} object.
	 * @throws io.spotnext.infrastructure.exception.ImportException if any.
	 */
	void importItems(ImportFormat format, ImportConfiguration config, InputStream inputStream) throws ImportException;

}
