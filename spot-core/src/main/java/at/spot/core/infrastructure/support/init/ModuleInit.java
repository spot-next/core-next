package at.spot.core.infrastructure.support.init;

import javax.annotation.Priority;
import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;

import at.spot.core.infrastructure.exception.ModuleInitializationException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.LoggingService;

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
	 * @param event
	 * @throws ModuleInitializationException
	 */
	@EventListener
	protected void onApplicationEvent(final ApplicationReadyEvent event) throws ModuleInitializationException {
		if (!alreadyInitialized) {
			initialize();
			alreadyInitialized = true;
		}

		if (configurationService.getBoolean("core.setup.import.initialdata", false)) {
			importInitialData();
		}

		if (configurationService.getBoolean("core.setup.import.sampledata", false)) {
			importSampleData();
		}

		loggingService.info("Initialization complete");
	}

	/**
	 * This is a hook to customize the initialization process. It is called after
	 * {@link Bootstrap} all spring beans are initialized.
	 */
	protected abstract void initialize() throws ModuleInitializationException;

	/**
	 * This is only called, if the corresponding command line flag is also set
	 */
	protected void importInitialData() throws ModuleInitializationException {
		//
	}

	protected void importSampleData() throws ModuleInitializationException {
		//
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
