package io.spotnext.core.infrastructure.service.impl;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Service
@SuppressFBWarnings(value = { "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE" })
public abstract class BeanAware implements BeanNameAware {
	private String beanName;

	@Autowired
	protected ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public BeanDefinitionRegistry getBeanFactory() {
		return (BeanDefinitionRegistry) ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
	}

	@Override
	public void setBeanName(final String beanName) {
		this.beanName = beanName;
	}

	public String getBeanName() {
		return this.beanName;
	}
}
