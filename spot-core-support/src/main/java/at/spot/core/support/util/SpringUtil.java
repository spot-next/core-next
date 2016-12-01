package at.spot.core.support.util;

import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class SpringUtil {
	/**
	 * Registers a new bean of the given type in the given spring context.
	 * 
	 * @param context
	 * @param beanType
	 * @param beanId
	 *            if this is not empty it will override the default bean id
	 * @param singleton
	 */
	public static void registerBean(final BeanDefinitionRegistry beanFactory, final Class<?> type, final String beanId,
			final boolean singleton) {

		String scope = null;

		if (singleton) {
			scope = "singleton";
		}

		registerBean(beanFactory, type, beanId, scope, null);
	}

	public static void registerBean(final BeanDefinitionRegistry beanFactory, final Class<?> type, final String beanId,
			final String scope, final List<? extends Object> constructorArguments) {

		final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(type);
		beanDefinition.setLazyInit(false);
		beanDefinition.setAbstract(Modifier.isAbstract(type.getModifiers()));
		beanDefinition.setAutowireCandidate(true);

		if (CollectionUtils.isNotEmpty(constructorArguments)) {
			final ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();

			for (final Object o : constructorArguments) {
				constructorArgs.addGenericArgumentValue(o);
			}

			beanDefinition.setConstructorArgumentValues(constructorArgs);
		}

		String id = type.getSimpleName();

		// use the annotated itemtype name, it should
		if (StringUtils.isNotBlank(beanId)) {
			id = beanId;
		}

		if (StringUtils.isNotBlank(scope)) {
			beanDefinition.setScope(scope);
		}

		beanFactory.registerBeanDefinition(id, beanDefinition);
	}
}
