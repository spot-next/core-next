package at.spot.core.infrastructure.service.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import at.spot.core.infrastructure.annotation.model.Type;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.Item;

public abstract class AbstractModelService extends AbstractService implements ModelService {
	@Resource(name = "modelPackageScanPaths")
	protected List<String> modelPackageScanPaths;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Override
	public List<Class<? extends Item>> getAvailableTypes() {
		Map<String, Item> types = getApplicationContext().getBeansOfType(Item.class);

		List<Class<? extends Item>> allTypes = new ArrayList<>(types.keySet().size());

		for (Item i : types.values()) {
			if (typeService.hasAnnotation(i.getClass(), Type.class)) {
				allTypes.add(i.getClass());
			}
		}

		return allTypes;
	}

	@Override
	public <T extends Item> T create(Class<T> type) {
		return getApplicationContext().getBean(type);
	}

	@Override
	public void registerTypes() {

		for (Class<?> clazz : typeService.getItemConcreteTypes(modelPackageScanPaths)) {
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

		getBeanFactory().registerBeanDefinition(type.getSimpleName(), beanDefinition);

		loggingService.debug(String.format("Registering type: %s", type.getSimpleName()));
	}

}
