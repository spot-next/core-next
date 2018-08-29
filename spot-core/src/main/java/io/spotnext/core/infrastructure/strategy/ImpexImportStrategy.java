package io.spotnext.core.infrastructure.strategy;

import java.io.InputStream;

import io.spotnext.core.infrastructure.exception.ImpexImportException;
import io.spotnext.itemtype.core.beans.ImportConfiguration;

/**
 * <p>ImpexImportStrategy interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ImpexImportStrategy {
	/**
	 * <p>importImpex.</p>
	 *
	 * @param config a {@link io.spotnext.itemtype.core.beans.ImportConfiguration} object.
	 * @param inputStream a {@link java.io.InputStream} object.
	 * @throws io.spotnext.core.infrastructure.exception.ImpexImportException if any.
	 */
	void importImpex(ImportConfiguration config, InputStream inputStream) throws ImpexImportException;
}
