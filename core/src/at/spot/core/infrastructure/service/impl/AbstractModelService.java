package at.spot.core.infrastructure.service.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import at.spot.core.ApplicationLoader;
import at.spot.core.infrastructure.annotation.model.Type;
import at.spot.core.infrastructure.service.ClasspathService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;

public abstract class AbstractModelService implements ModelService {
	@Autowired
	protected List<String> modelPackageScanPaths;

	@Autowired
	protected ClasspathService classpathService;

	@Autowired
	protected LoggingService loggingService;

	@Override
	public List<Class<? extends Item>> getAvailableTypes() {
		Map<String, Item> types = ApplicationLoader.getApplicationContext().getBeansOfType(Item.class);

		List<Class<? extends Item>> allTypes = new ArrayList<>(types.keySet().size());

		for (Item i : types.values()) {
			if (classpathService.hasAnnotation(i.getClass(), Type.class)) {
				allTypes.add(i.getClass());
			}
		}

		return allTypes;
	}

	@Override
	public <T extends Item> T create(Class<T> type) {
		return ApplicationLoader.getApplicationContext().getBean(type);
	}

	@Override
	public void registerTypes() {

		for (Class<?> clazz : classpathService.getItemConcreteTypes(modelPackageScanPaths)) {
			if (clazz.isAnnotationPresent(Type.class)) {
				registerType(clazz, "prototype");
			}
		}
	}

	protected void registerType(Class<?> type, String scope) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(type);
		beanDefinition.setLazyInit(false);
		beanDefinition.setAbstract(Modifier.isAbstract(type.getModifiers()));
		beanDefinition.setAutowireCandidate(true);
		beanDefinition.setScope(scope);

		ApplicationLoader.getBeanFactory().registerBeanDefinition(type.getSimpleName(), beanDefinition);

		loggingService.debug(String.format("Registering type: %s", type.getSimpleName()));
	}

}
