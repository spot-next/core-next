package io.spotnext.core;

import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import ch.qos.logback.core.util.CloseUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.service.ImportService;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.enumeration.ImportFormat;

/**
 * This is the main entry point for the application. After the application has
 * been initialized, {@link io.spotnext.core.CoreInit#initialize()},
 * {@link io.spotnext.core.CoreInit#importInitialData()} and {@link io.spotnext.core.CoreInit#importSampleData()}
 * will be called in that exact order.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@ImportResource("classpath:/core-spring.xml")
@PropertySource(value = "classpath:/core.properties")
@PropertySource(value = "classpath:/local.properties", ignoreResourceNotFound = true)
public class CoreInit extends ModuleInit {

	@Resource
	protected ImportService importService;

	
	/*
	 * STARTUP FUNCTIONALITY
	 */

	@Override
	protected void initialize() throws ModuleInitializationException {
		//
	}

	@Override
	protected void importInitialData() throws ModuleInitializationException {
		super.importInitialData();

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
	protected void importSampleData() throws ModuleInitializationException {
		super.importSampleData();

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
