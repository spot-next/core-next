package at.spot.core.infrastructure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractService {

	@Autowired
	protected ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public BeanDefinitionRegistry getBeanFactory() {
		return (BeanDefinitionRegistry) ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
	}
}