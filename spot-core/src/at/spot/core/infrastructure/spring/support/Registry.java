package at.spot.core.infrastructure.spring.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.persistence.service.PersistenceService;

/**
 * This is a static provider for the current spring context. It also provides
 * some getters for commonly used Services.
 *
 */
public class Registry {

	private static ApplicationContext context;

	private static ModelService modelService;
	private static TypeService typeService;
	private static LoggingService loggingService;
	private static PersistenceService persistenceService;

	public static void setApplicationContext(ApplicationContext ctx) throws BeansException {
		context = ctx;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static BeanDefinitionRegistry getBeanFactory() {
		return (BeanDefinitionRegistry) ((ConfigurableApplicationContext) context).getBeanFactory();
	}

	/*
	 * SOME HELPER GETTERS FOR COMMONLY USED SERVICES
	 */

	public static PersistenceService getPersistenceService() {
		if (persistenceService == null)
			persistenceService = (PersistenceService) Registry.getApplicationContext().getBean("persistenceService");

		return persistenceService;
	}

	public static TypeService getTypeService() {
		if (typeService == null)
			typeService = (TypeService) Registry.getApplicationContext().getBean("typeService");

		return typeService;
	}

	public static ModelService getModelService() {
		if (modelService == null)
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");

		return modelService;
	}

	public static LoggingService getLoggingService() {
		if (loggingService == null)
			loggingService = (LoggingService) Registry.getApplicationContext().getBean("loggingService");
		;

		return loggingService;
	}

	public static <T> T getBean(String beanName, Class<T> beanType) {
		return context.getBean(beanName, beanType);
	}
}
