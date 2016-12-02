package at.spot.core.support.util;

import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class SpringUtil {

	public static enum BeanScope {
		prototype, singleton,
	}

	/**
	 * Registers a new bean of the given type in the given spring context.
	 * 
	 * @param context
	 * @param type
	 * @param beanId
	 *            if this is not empty it will override the default bean id
	 * @param scope
	 * @constructorArguments
	 */
	public static void registerBean(final BeanDefinitionRegistry beanFactory, final Class<?> type, final String beanId,
			final BeanScope scope, final List<? extends Object> constructorArguments, final boolean lazyInit) {

		final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(type);
		beanDefinition.setLazyInit(lazyInit);
		beanDefinition.setAbstract(Modifier.isAbstract(type.getModifiers()));
		beanDefinition.setAutowireCandidate(false);

		if (CollectionUtils.isNotEmpty(constructorArguments)) {
			final ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();

			for (final Object o : constructorArguments) {
				constructorArgs.addGenericArgumentValue(new ValueHolder(o));
			}

			beanDefinition.setConstructorArgumentValues(constructorArgs);
		}

		String id = type.getSimpleName();

		// use the annotated itemtype name, it should
		if (StringUtils.isNotBlank(beanId)) {
			id = beanId;
		}

		if (scope != null) {
			beanDefinition.setScope(scope.toString());
		}

		beanFactory.registerBeanDefinition(id, beanDefinition);
	}
}
