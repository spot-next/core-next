package at.spot.core;

import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.User;
import at.spot.core.persistence.service.PersistenceService;

@Service
public class ApplicationLoader {

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		try {
			applicationContext = new ClassPathXmlApplicationContext("spring.xml");

			ApplicationLoader loader = applicationContext.getBean(ApplicationLoader.class);
			loader.configure();
			loader.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * STARTUP FUNCTIONALITY
	 */

	@Log(logLevel = LogLevel.DEBUG, before = true, measureTime = true)
	protected void configure() {
		getModelService().registerTypes();
		getPersistenceService().initDataStorage();
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

	protected ModelService getModelService() {
		return (ModelService) applicationContext.getBean("modelService");
	}

	protected LoggingService getLoggingService() {
		return (LoggingService) applicationContext.getBean("loggingService");
	}
}
