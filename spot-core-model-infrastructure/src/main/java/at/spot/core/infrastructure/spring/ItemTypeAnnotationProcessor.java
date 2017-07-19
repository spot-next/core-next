package at.spot.core.infrastructure.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;

/**
 * Registers an alias for each {@link Item} bean using the
 * {@link ItemType#typeCode()}.
 */
@Service
public class ItemTypeAnnotationProcessor implements BeanPostProcessor {

	private final ConfigurableListableBeanFactory configurableBeanFactory;

	@Autowired
	public ItemTypeAnnotationProcessor(final ConfigurableListableBeanFactory beanFactory) {
		this.configurableBeanFactory = beanFactory;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		if (bean instanceof Item) {
			final ItemType typeDefinition = ClassUtil.getAnnotation(bean.getClass(), ItemType.class);

			configurableBeanFactory.registerAlias(beanName, typeDefinition.typeCode());
		}

		return bean;
	}

}