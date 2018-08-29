package io.spotnext.core.infrastructure.support.init;

import javax.annotation.Priority;
import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListenerMethodProcessor;

import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.support.spring.HierarchyAwareEventListenerMethodProcessor;

/**
 * <p>
 * The base ModuleInit class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@PropertySource(value = "classpath:/git.properties", ignoreResourceNotFound = true)
@Configuration
@Priority(value = -1)
// needed to avoid some spring/hibernate problems
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class })
public abstract class ModuleInit implements ApplicationContextAware {

	protected ApplicationContext applicationContext;
	protected boolean alreadyInitialized = false;

	@Resource
	protected ConfigurationService configurationService;

	@Resource
	protected LoggingService loggingService;

	/**
	 * Called when the spring application context has been initialized.
	 * 
	 * @param event the spring application event name
	 * @throws ModuleInitializationException in case there is an exception during
	 *                                       post initialization
	 */
	@EventListener
	protected void onApplicationEvent(final ApplicationReadyEvent event) throws ModuleInitializationException {
		if (!alreadyInitialized) {
			initialize();
			if (configurationService.getBoolean("core.setup.import.initialdata", false)) {
				importInitialData();
			}

			if (configurationService.getBoolean("core.setup.import.sampledata", false)) {
				importSampleData();
			}

			loggingService.info("Initialization complete");
			alreadyInitialized = true;
		}
	}

	/**
	 * This is a hook to customize the initialization process. It is called after
	 * {@link Bootstrap} all spring beans are initialized.
	 * 
	 * @throws ModuleInitializationException if there is any unexpected error
	 */
	@Log(message = "Initializing module $classSimpleName", measureTime = true)
	protected abstract void initialize() throws ModuleInitializationException;

	/**
	 * Imports the initial data, if any are present.
	 * 
	 * @throws ModuleInitializationException in case there is any error
	 */
	@Log(message = "Importing initial data for $classSimpleName", measureTime = true)
	protected void importInitialData() throws ModuleInitializationException {
		//
	}

	/**
	 * Imports the sample data, if any are present.
	 * 
	 * @throws ModuleInitializationException in case there is any error
	 */
	@Log(message = "Importing sample data for $classSimpleName", measureTime = true)
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
	 * Override Spring's {@link EventListenerMethodProcessor} and always use the
	 * root spring context. This makes sure that all event listeners (even in child
	 * contexts) get notified when events in a parent context are thrown.
	 * 
	 * @return the custom event listener instance
	 */
	@Bean(name = "org.springframework.context.event.internalEventListenerProcessor")
	protected EventListenerMethodProcessor eventListenerMethodProcessor() {
		final EventListenerMethodProcessor processor = new HierarchyAwareEventListenerMethodProcessor();

		return processor;
	}

	/**
	 * @return true if post initialization has been finished.
	 */
	public boolean isAlreadyInitialized() {
		return alreadyInitialized;
	}
}
