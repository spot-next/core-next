package io.spotnext.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.persistence.service.QueryService;

/**
 * This is the main entry point for the application. After the application has been initialized, {@link io.spotnext.core.CoreInit#initialize()},
 * {@link io.spotnext.core.CoreInit#importInitialData()} and {@link io.spotnext.core.CoreInit#importSampleData()} will be called in that exact order.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@EnableCaching
//@EnableAspectJAutoProxy
@EnableLoadTimeWeaving
@ImportResource("classpath:/core-spring.xml")
@PropertySource(value = "classpath:/core.properties")
@PropertySource(value = "classpath:/local.properties", ignoreResourceNotFound = true)
@SpringBootApplication
public class CoreInit extends ModuleInit {

	@Autowired
	QueryService queryService;

	/*
	 * STARTUP FUNCTIONALITY
	 */

	@Override
	protected void initialize() throws ModuleInitializationException {
		//
	}

	@Log(message = "Importing initial data for $classSimpleName", measureExecutionTime = true)
	@Override
	protected void importInitialData() throws ModuleInitializationException {
		super.importInitialData();

		importScript("/data/initial/countries.impex", "Importing countries");
		importScript("/data/initial/languages.impex", "Importing languages");
		importScript("/data/initial/currencies.impex", "Importing currencies");
		importScript("/data/initial/users.impex", "Importing users");
		importScript("/data/initial/catalogs.impex", "Importing catalogs");
	}

	// @SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION", justification = "Stream is closed in ImportService")
	@Log(message = "Importing sample data for $classSimpleName", measureExecutionTime = true)
	@Override
	protected void importSampleData() throws ModuleInitializationException {
		super.importSampleData();

		importScript("/data/sample/users.impex", "Importing sample users");
		importScript("/data/sample/medias.impex", "Importing sample medias");
	}

	public static void main(String... args) {
		ModuleInit.bootstrap(CoreInit.class, args);
	}
}
