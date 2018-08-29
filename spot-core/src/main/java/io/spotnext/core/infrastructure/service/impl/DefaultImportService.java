package io.spotnext.core.infrastructure.service.impl;

import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.core.infrastructure.service.ImportService;
import io.spotnext.core.infrastructure.strategy.ImpexImportStrategy;
import io.spotnext.core.support.util.ValidationUtil;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.enumeration.ImportFormat;
import ch.qos.logback.core.util.CloseUtil;

/**
 * <p>DefaultImportService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultImportService implements ImportService {

	@Resource
	protected ImpexImportStrategy impexImportStrategy;

	/** {@inheritDoc} */
	@Override
	public void importItems(ImportFormat format, ImportConfiguration config, InputStream inputStream)
			throws ImportException {

		ValidationUtil.validateNotNull("Input stream must not be null", inputStream);

		try {
			if (ImportFormat.ImpEx.equals(format)) {
				impexImportStrategy.importImpex(config, inputStream);
			} else {
				throw new ImportException(String.format("Format %s is currently not implemented", format));
			}
		} finally {
			CloseUtil.closeQuietly(inputStream);
		}
	}

}
