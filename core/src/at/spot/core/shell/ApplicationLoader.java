package at.spot.core.shell;

import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.Bootstrap;

import at.spot.core.data.model.User;
import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.persistence.service.PersistenceService;

/**
 * This is the main entry point for the application. After the application has
 * been initialized, it will call {@link ApplicationLoader#run()}. Then the
 * shell is being loaded.
 */
public class ApplicationLoader {

	private ApplicationContext applicationContext;

	ModelService modelService;
	TypeService typeService;
	LoggingService loggingService;

	public static void main(String[] args) throws Exception {
		ApplicationLoader loader = new ApplicationLoader();
		loader.init();
		loader.startShell(args);
	}

	public void run() {
		for (int i = 0; i < 100; i++) {
			User user = getModelService().create(User.class);
			user.name = "test-" + i;
			user.uid = user.name;

			try {
				getModelService().save(user);
			} catch (ModelSaveException e) {
				getLoggingService().exception(e.getMessage());
			}
		}

		try {
			User loadedUser = getModelService().get(User.class, 0l);
			getLoggingService().info("loaded user again");
		} catch (ModelNotFoundException e) {
			getLoggingService().exception(e.getMessage());
		}

		getPersistenceService().saveDataStorage();

		getLoggingService().info("Exited");
	}

	/*
	 * STARTUP FUNCTIONALITY
	 */

	public void init() throws Exception {
		initSpring();
		setupTypeInfrastrucutre();

		run();
	}

	public void initSpring() {
		applicationContext = new ClassPathXmlApplicationContext("spring.xml");
	}

	@Log(logLevel = LogLevel.DEBUG, before = true, measureTime = false)
	protected void setupTypeInfrastrucutre() {
		getTypeService().registerTypes();
		getPersistenceService().initDataStorage();
	}

	@Log(logLevel = LogLevel.DEBUG, before = true, measureTime = false)
	public void startShell(String... args) throws Exception {
		Bootstrap.main(args);
	}

	/*
	 * PROPERTIES
	 */

	// public static ApplicationContext getApplicationContext() {
	// return applicationContext;
	// }

	/*
	 * INTERNAL FUNCTIONALITY
	 */

	// public static BeanDefinitionRegistry getBeanFactory() {
	// return (BeanDefinitionRegistry) ((ConfigurableApplicationContext)
	// applicationContext).getBeanFactory();
	// }

	/*
	 * SPRING SETTERS
	 */

	protected PersistenceService getPersistenceService() {
		return (PersistenceService) applicationContext.getBean("persistenceService");
	}

	protected TypeService getTypeService() {
		if (typeService == null)
			typeService = (TypeService) applicationContext.getBean("typeService");
		return typeService;
	}

	protected ModelService getModelService() {
		if (modelService == null)
			modelService = (ModelService) applicationContext.getBean("modelService");
		return modelService;
	}

	protected LoggingService getLoggingService() {
		return (LoggingService) applicationContext.getBean("loggingService");
	}
}
