package io.spotnext.core.infrastructure.strategy;

import java.io.InputStream;

import io.spotnext.core.infrastructure.exception.ImpexImportException;
import io.spotnext.itemtype.core.beans.ImportConfiguration;

public interface ImpexImportStrategy {
	void importImpex(ImportConfiguration config, InputStream inputStream) throws ImpexImportException;
}
