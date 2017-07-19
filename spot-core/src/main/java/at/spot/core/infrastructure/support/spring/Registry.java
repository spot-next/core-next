package at.spot.core.infrastructure.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.init.Configuration;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.persistence.service.PersistenceService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is a static provider for the current spring context. It also provides
 * some getters for commonly used Services.
 *
 */
@Service
@Order(value = 0)
@SuppressFBWarnings({ "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE", "LI_LAZY_INIT_STATIC",
		"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD" })
public class Registry implements ApplicationContextAware {

	private static ApplicationContext context;
	private static Thread mainThread;
	private static Class<? extends ModuleInit> mainClass;

	private static ModelService modelService;
	private static TypeService typeService;
	private static LoggingService loggingService;
	private static PersistenceService persistenceService;
	private static Configuration configuration;

	public static Class<? extends ModuleInit> getMainClass() {
		return mainClass;
	}

	public static void setMainClass(final Class<? extends ModuleInit> mainClass) {
		Registry.mainClass = mainClass;
	}

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		context = ctx;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static BeanDefinitionRegistry getBeanFactory() {
		return (BeanDefinitionRegistry) ((ConfigurableApplicationContext) context).getBeanFactory();
	}

	public static Thread getMainThread() {
		return mainThread;
	}

	public static void setMainThread(final Thread mainThread) {
		Registry.mainThread = mainThread;
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

	public static <T> T getBean(final String beanName, final Class<T> beanType) {
		return context.getBean(beanName, beanType);
	}

	public static void setAppConfiguration(final Configuration configuration) {
		Registry.configuration = configuration;
	}

	public static Configuration getAppConfiguration() {
		return configuration;
	}
}
