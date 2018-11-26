package io.spotnext.core.infrastructure.support.init;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.GenericApplicationContext;

import ch.qos.logback.core.util.CloseUtil;
import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.ImportException;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.ImportService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.spring.HierarchyAwareEventListenerMethodProcessor;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.infrastructure.instrumentation.JpaEntityClassTransformer;
import io.spotnext.instrumentation.DynamicInstrumentationLoader;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;

/**
 * <p>
 * The base ModuleInit class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@DependsOn("persistenceService")
@PropertySource(value = "classpath:/git.properties", ignoreResourceNotFound = true)
@Configuration
@Priority(value = -1)
// needed to avoid some spring/hibernate problems
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
public abstract class ModuleInit implements ApplicationContextAware, PostConstructor {

	protected static final String BEANNAME_EVENT_LISTENER_PROCESSOR = "org.springframework.context.event.internalEventListenerProcessor";

	protected ApplicationContext applicationContext;
	protected boolean alreadyInitialized = false;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected ImportService importService;

	/**
	 * Called when the spring application context has been initialized.
	 * 
	 * @param event the spring application event name
	 * @throws ModuleInitializationException in case there is an exception during post initialization
	 */
	@Override
	public void setup() throws ModuleInitializationException {
		if (!alreadyInitialized) {
			initialize();
			if (configurationService.getBoolean("core.setup.import.initialdata", false)) {
				importInitialData();
			}

			if (configurationService.getBoolean("core.setup.import.sampledata", false)) {
				importSampleData();
			}

			Logger.info("Initialization complete");
			alreadyInitialized = true;
		}
	}

	/**
	 * This is a hook to customize the initialization process. It is called after {@link Bootstrap} all spring beans are initialized.
	 * 
	 * @throws ModuleInitializationException if there is any unexpected error
	 */
	@Log(message = "Initializing module $classSimpleName", measureExecutionTime = true)
	protected abstract void initialize() throws ModuleInitializationException;

	/**
	 * Imports the initial data, if any are present.
	 * 
	 * @throws ModuleInitializationException in case there is any error
	 */
//	@Logger(message = "Importing initial data for $classSimpleName", measureTime = true)
	protected void importInitialData() throws ModuleInitializationException {
		//
	}

	/**
	 * Imports the sample data, if any are present.
	 * 
	 * @throws ModuleInitializationException in case there is any error
	 */
//	@Logger(message = "Importing sample data for $classSimpleName", measureTime = true)
	protected void importSampleData() throws ModuleInitializationException {
		//
	}

	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the spring application context for this module.
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Override Spring's {@link EventListenerMethodProcessor} and always use the root spring context. This makes sure that all event listeners (even in child
	 * contexts) get notified when events in a parent context are thrown.
	 * 
	 * @return the custom event listener instance
	 */
	@Bean(name = "org.springframework.context.event.internalEventListenerProcessor")
	protected static EventListenerMethodProcessor eventListenerMethodProcessor() {
		final EventListenerMethodProcessor processor = new HierarchyAwareEventListenerMethodProcessor();

		return processor;
	}

	protected ListableBeanFactory getBeanFactory() {
		if (applicationContext instanceof GenericApplicationContext) {
			return ((GenericApplicationContext) applicationContext).getBeanFactory();
		}

		throw new IllegalStateException("Unexpected application context type.");
	}

	/**
	 * @return true if post initialization has been finished.
	 */
	public boolean isAlreadyInitialized() {
		return alreadyInitialized;
	}

	protected void importScript(final String path, final String logMessage) throws ImportException {
		Logger.debug(logMessage);

		InputStream stream = null;
		try {
			final ImportConfiguration conf = new ImportConfiguration();
			conf.setIgnoreErrors(false);
			conf.setScriptIdentifier(path);
			conf.setFormat(DataFormat.ImpEx);

			stream = CoreInit.class.getResourceAsStream(conf.getScriptIdentifier());
			importService.importItems(conf, stream);
		} finally {
			CloseUtil.closeQuietly(stream);
		}
	}

	/**
	 * Initializes the load time weaving support and registers the necessary classtransformers.
	 */
	public static void initializeWeavingSupport() {
		// weave all classes before they are loaded as beans
		Logger.debug("Initializing weaving support");
		DynamicInstrumentationLoader.initialize(JpaEntityClassTransformer.class);
	}

	public static void bootstrap(Class<? extends ModuleInit> init, String... commandLineArgs) {
		bootstrap(init, null, commandLineArgs);
	}

	public static void bootstrap(Class<? extends ModuleInit> parentInit, Class<? extends ModuleInit> childInit, String... commandLineArgs) {
		initializeWeavingSupport();
		Registry.setMainClass(childInit != null ? childInit : parentInit);

		SpringApplicationBuilder builder = new SpringApplicationBuilder(parentInit).addCommandLineProperties(true);

		if (childInit != null) {
			builder = builder.child(childInit).addCommandLineProperties(true);
		}

		builder.build(prepareCommandLineArguments(commandLineArgs)).run(prepareCommandLineArguments(commandLineArgs));
	}

	/**
	 * Inject the "allow bean override" setting otherwise spring-boot doesn't run
	 * 
	 * @param commandLineArgs the original command line args
	 * @return the customized arguments
	 */
	private static String[] prepareCommandLineArguments(String... commandLineArgs) {

		final Map<String, String> parsedArgs = new HashMap<>();

		if (commandLineArgs != null) {
			for (String a : commandLineArgs) {
				String[] split = StringUtils.split(a, "=");

				if (StringUtils.trimToEmpty(split[0]).length() > 0) {
					parsedArgs.put(split[0], split.length > 1 && StringUtils.trimToEmpty(split[1]).length() > 1 ? split[1] : null);
				}
			}
		}

		parsedArgs.put("--spring.main.allow-bean-definition-overriding", "true");

		final List<String> args = parsedArgs.entrySet().stream() //
				.map(e -> e.getKey() + (e.getValue() != null ? "=" + e.getValue() : "")) //
				.collect(Collectors.toList());

		return args.toArray(new String[args.size()]);
	}
}
