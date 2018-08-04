package at.spot.core;

import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ImportException;
import at.spot.core.infrastructure.exception.ModuleInitializationException;
import at.spot.core.infrastructure.service.ImportService;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.itemtype.core.beans.ImportConfiguration;
import at.spot.itemtype.core.enumeration.ImportFormat;
import ch.qos.logback.core.util.CloseUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is the main entry point for the application. After the application has
 * been initialized, {@link CoreInit#initialize()},
 * {@link CoreInit#importInitialData()} and {@link CoreInit#importSampleData()}
 * will be called in that exact order.
 */
@ImportResource("classpath:/core-spring.xml")
@PropertySource(value = "classpath:/core.properties")
@PropertySource(value = "classpath:/local.properties", ignoreResourceNotFound = true)
// @EnableAsync
// @EnableTransactionManagement
// @EnableScheduling
// @EnableJpaAuditing
public class CoreInit extends ModuleInit {

	@Resource
	protected ImportService importService;

	/*
	 * STARTUP FUNCTIONALITY
	 */

	@Override
	@Log(message = "Initializing core", measureTime = true)
	protected void initialize() throws ModuleInitializationException {
		//
	}

	@Override
	@Log(message = "Importing initial data", measureTime = true)
	protected void importInitialData() throws ModuleInitializationException {
		try {
			importScript("/data/initial/countries.impex", "Importing countries");
			importScript("/data/initial/languages.impex", "Importing languages");
			importScript("/data/initial/currencies.impex", "Importing currencies");
			importScript("/data/initial/users.impex", "Importing users");
			importScript("/data/initial/catalogs.impex", "Importing catalogs");
		} catch (final ImportException e) {
			loggingService.warn("Could not import initial data: " + e.getMessage());
		}
	}

	@SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION", justification = "Stream is closed in ImportService")
	@Override
	@Log(message = "Importing sample data", measureTime = true)
	protected void importSampleData() throws ModuleInitializationException {
		try {
			importScript("/data/sample/users.impex", "Importing sample users");
			importScript("/data/sample/medias.impex", "Importing sample medias");
		} catch (final ImportException e) {
			loggingService.warn("Could not import initial data: " + e.getMessage());
		}
	}

	private void importScript(final String path, final String logMessage) throws ImportException {
		loggingService.debug(logMessage);

		InputStream stream = null;
		try {
			final ImportConfiguration conf = new ImportConfiguration();
			conf.setIgnoreErrors(true);
			conf.setScriptIdentifier(path);

			stream = CoreInit.class.getResourceAsStream(conf.getScriptIdentifier());
			importService.importItems(ImportFormat.ImpEx, conf, stream);
		} finally {
			CloseUtil.closeQuietly(stream);
		}
	}
}
