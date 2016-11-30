package at.spot.core.infrastructure.init;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public abstract class ModuleInit {

	@Autowired
	protected ConfigurationHolder configHolder;

	public ModuleInit() {
		// ConfigurationHolder holder = Reg
		System.out.println("");
	}

	/**
	 * Initializes a module with the given parent application context (from the
	 * {@link Bootstrap}).
	 * 
	 * @param parentContext
	 */
	@PostConstruct
	public void init() {
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
