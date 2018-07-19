package at.spot.core.infrastructure.strategy;

import java.io.InputStream;

import at.spot.core.infrastructure.exception.ImpexImportException;
import at.spot.itemtype.core.beans.ImportConfiguration;

public interface ImpexImportStrategy {
	void importImpex(ImportConfiguration config, InputStream inputStream) throws ImpexImportException;
}
