package io.spotnext.core.infrastructure.service.impl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>Abstract BeanAware class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
//@SuppressFBWarnings(value = { "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE" })
public abstract class BeanAware implements BeanNameAware {
	private String beanName;

	@Autowired
	protected ApplicationContext applicationContext;

	/**
	 * <p>Getter for the field <code>applicationContext</code>.</p>
	 *
	 * @return a {@link org.springframework.context.ApplicationContext} object.
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * <p>Setter for the field <code>applicationContext</code>.</p>
	 *
	 * @param applicationContext a {@link org.springframework.context.ApplicationContext} object.
	 */
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * <p>getBeanFactory.</p>
	 *
	 * @return a {@link org.springframework.beans.factory.support.BeanDefinitionRegistry} object.
	 */
	public BeanDefinitionRegistry getBeanFactory() {
		return (BeanDefinitionRegistry) ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
	}

	/** {@inheritDoc} */
	@Override
	public void setBeanName(final String beanName) {
		this.beanName = beanName;
	}

	/**
	 * <p>Getter for the field <code>beanName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBeanName() {
		return this.beanName;
	}
}
