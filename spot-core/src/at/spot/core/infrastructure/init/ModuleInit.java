package at.spot.core.infrastructure.init;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public abstract class ModuleInit {
	/**
	 * Initializes a module with the given parent application context (from the
	 * {@link Bootstrap}).
	 * 
	 * @param parentContext
	 */
	@PostConstruct
	public abstract void initialize();

	/**
	 * Inject a bean definition using a {@link BeanDefinitionReader}. This is
	 * necessary, so that the spring context of this module can be merged with
	 * the parent context.
	 * 
	 * @param parentContext
	 */
	public abstract void injectBeanDefinition(BeanDefinitionRegistry parentContext);
}
