package io.spotnext.spring.web.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.support.spring.Registry;

/**
 * <p>Abstract AbstractBaseController class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractBaseController implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	protected LoggingService loggingService;

	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(final ApplicationContext context) throws BeansException {
		this.applicationContext = context;
	}

	/**
	 * <p>getWebApplicationContext.</p>
	 *
	 * @return a {@link org.springframework.context.ApplicationContext} object.
	 */
	public ApplicationContext getWebApplicationContext() {
		return this.applicationContext;
	}

	protected <T> T getBean(final Class<T> beanType) {
		return Registry.getApplicationContext().getBean(beanType);
	}

	protected <T> T getBean(final Class<T> beanType, final String beanName) {
		return Registry.getApplicationContext().getBean(beanName, beanType);
	}
}
