package io.spotnext.core.infrastructure.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.persistence.service.PersistenceService;

/**
 * This is a static provider for the current spring context. It also provides
 * some getters for commonly used Services.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
@Order(value = 0)
//@SuppressFBWarnings({ "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE", "LI_LAZY_INIT_STATIC",
//		"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD" })
public class Registry implements ApplicationContextAware {
	private static Registry INSTANCE = new Registry();
	
	private static ApplicationContext context;
	private static Thread mainThread;
	private static Class<? extends ModuleInit> mainClass = CoreInit.class;

	private static ModelService modelService;
	private static TypeService typeService;
	private static LoggingService loggingService;
	private static PersistenceService persistenceService;

	private Registry() {
		INSTANCE = this;
	}
	
	public static Registry instance() {
		return INSTANCE;
	}
	
	/**
	 * <p>Getter for the field <code>mainClass</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public static Class<? extends ModuleInit> getMainClass() {
		return mainClass;
	}

	/**
	 * <p>Setter for the field <code>mainClass</code>.</p>
	 *
	 * @param mainClass a {@link java.lang.Class} object.
	 */
	public static void setMainClass(final Class<? extends ModuleInit> mainClass) {
		Registry.mainClass = mainClass;
	}

	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		context = ctx;
	}
	
	/**
	 * <p>getApplicationContext.</p>
	 *
	 * @return a {@link org.springframework.context.ApplicationContext} object.
	 */
	public static ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * <p>getBeanFactory.</p>
	 *
	 * @return a {@link org.springframework.beans.factory.support.BeanDefinitionRegistry} object.
	 */
	public static BeanDefinitionRegistry getBeanFactory() {
		return (BeanDefinitionRegistry) ((ConfigurableApplicationContext) context).getBeanFactory();
	}

	/**
	 * <p>Getter for the field <code>mainThread</code>.</p>
	 *
	 * @return a {@link java.lang.Thread} object.
	 */
	public static Thread getMainThread() {
		return mainThread;
	}

	/**
	 * <p>Setter for the field <code>mainThread</code>.</p>
	 *
	 * @param mainThread a {@link java.lang.Thread} object.
	 */
	public static void setMainThread(final Thread mainThread) {
		Registry.mainThread = mainThread;
	}

	/*
	 * SOME HELPER GETTERS FOR COMMONLY USED SERVICES
	 */

	/**
	 * <p>Getter for the field <code>persistenceService</code>.</p>
	 *
	 * @return a {@link io.spotnext.core.persistence.service.PersistenceService} object.
	 */
	public static PersistenceService getPersistenceService() {
		if (persistenceService == null)
			persistenceService = (PersistenceService) Registry.getApplicationContext().getBean("persistenceService");

		return persistenceService;
	}

	/**
	 * <p>Getter for the field <code>typeService</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.service.TypeService} object.
	 */
	public static TypeService getTypeService() {
		if (typeService == null)
			typeService = (TypeService) Registry.getApplicationContext().getBean("typeService");

		return typeService;
	}

	/**
	 * <p>Getter for the field <code>modelService</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.service.ModelService} object.
	 */
	public static ModelService getModelService() {
		if (modelService == null)
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");

		return modelService;
	}

	/**
	 * <p>Getter for the field <code>loggingService</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.service.LoggingService} object.
	 */
	public static LoggingService getLoggingService() {
		if (loggingService == null)
			loggingService = (LoggingService) Registry.getApplicationContext().getBean("loggingService");

		return loggingService;
	}

	/**
	 * <p>getBean.</p>
	 *
	 * @param beanName a {@link java.lang.String} object.
	 * @param beanType a {@link java.lang.Class} object.
	 * @param <T> a T object.
	 * @return a T object.
	 */
	public static <T> T getBean(final String beanName, final Class<T> beanType) {
		return context.getBean(beanName, beanType);
	}

	/**
	 * <p>shutdown.</p>
	 */
	public static void shutdown() {
		getLoggingService().warn("SHUTTING DOWN!");
		((AbstractApplicationContext) context).close();
	}

	/**
	 * <p>getBuildInfos.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.support.spring.BuildInfo} object.
	 */
	public static BuildInfo getBuildInfos() {
		return (BuildInfo) Registry.getApplicationContext().getBean("buildInfo");
	}
}
