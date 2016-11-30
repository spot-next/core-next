package at.spot.core.infrastructure.init;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public abstract class ModuleInit {

	protected static Set<Properties> configProperties = new LinkedHashSet<>();

	/**
	 * Initializes a module with the given parent application context (from the
	 * {@link Bootstrap}).
	 * 
	 * @param parentContext
	 */
	@PostConstruct
	public void init() {
		configProperties.add(getConfiguration());
		initialize();
	}

	public abstract void initialize();

	/**
	 * Inject a bean definition using a {@link BeanDefinitionReader}. This is
	 * necessary, so that the spring context of this module can be merged with
	 * the parent context.
	 * 
	 * @param parentContext
	 */
	public abstract void injectBeanDefinition(BeanDefinitionRegistry parentContext);

	/**
	 * Returns the {@link Properties} for current {@link ModuleInit}.
	 * 
	 * @return
	 */
	public abstract Properties getConfiguration();
}
