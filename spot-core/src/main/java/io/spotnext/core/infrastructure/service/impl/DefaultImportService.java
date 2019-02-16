package io.spotnext.core.infrastructure.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.util.CloseUtil;
import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.core.infrastructure.service.ImportService;
import io.spotnext.core.infrastructure.strategy.ImpexImportStrategy;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;

/**
 * <p>
 * DefaultImportService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultImportService implements ImportService {

	@Autowired
	protected ImpexImportStrategy impexImportStrategy;

	/** {@inheritDoc} */
	@Override
	public void importItems(ImportConfiguration config, InputStream inputStream)
			throws ImportException {

		if (inputStream != null) {
			try {
				if (DataFormat.ImpEx.equals(config.getFormat())) {
					impexImportStrategy.importImpex(config, inputStream);
				} else {
					throw new ImportException(String.format("Format %s is currently not implemented", config.getFormat()));
				}
			} finally {
				CloseUtil.closeQuietly(inputStream);
			}
		} else {
			Logger.warn(String.format("Input stream for must not be null (identifier=%s, format=%s)", config.getScriptIdentifier(), config.getFormat()));
		}
	}

}
