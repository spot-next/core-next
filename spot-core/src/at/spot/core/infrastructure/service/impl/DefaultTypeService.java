package at.spot.core.infrastructure.service.impl;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.ModuleDefinition;
import at.spot.core.model.Item;

/**
 * Provides functionality to manage the typesystem.
 *
 */
@Service
public class DefaultTypeService extends AbstractService implements TypeService {

	@Autowired
	protected List<ModuleDefinition> moduleDefinitions;

	@Autowired
	protected LoggingService loggingService;

	/*
	 * *************************************************************************
	 * **************************** TYPE SYSTEM INIT ***************************
	 * *************************************************************************
	 */

	@Override
	public void registerTypes() {
		for (Class<?> clazz : getItemConcreteTypes(moduleDefinitions)) {
			if (clazz.isAnnotationPresent(ItemType.class)) {
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

		String beanName = type.getSimpleName();

		ItemType ann = type.getAnnotation(ItemType.class);

		// use the annotated itemtype name, it should
		if (ann != null && StringUtils.isNotBlank(ann.typeCode())) {
			beanName = ann.typeCode();
		}

		getBeanFactory().registerBeanDefinition(beanName, beanDefinition);

		loggingService.debug(String.format("Registering type: %s", type.getSimpleName()));
	}

	/*
	 * *************************************************************************
	 * **************************** ANNOTATIONS
	 * *************************************************************************
	 * *************************
	 */

	@Override
	public <A extends Annotation> boolean hasAnnotation(JoinPoint joinPoint, Class<A> annotation) {
		return getAnnotation(joinPoint, annotation) != null;
	}

	public <A extends Annotation> A getAnnotation(JoinPoint joinPoint, Class<A> annotation) {
		A ret = null;

		Signature sig = joinPoint.getSignature();

		if (sig instanceof MethodSignature) {
			final MethodSignature methodSignature = (MethodSignature) sig;
			Method method = methodSignature.getMethod();

			if (method.getDeclaringClass().isInterface()) {
				try {
					method = joinPoint.getTarget().getClass().getMethod(methodSignature.getName());
				} catch (NoSuchMethodException | SecurityException e) {
					//
				}
			}

			ret = AnnotationUtils.findAnnotation(method, annotation);
		} else {
			FieldSignature fieldSignature = (FieldSignature) sig;

			ret = fieldSignature.getField().getAnnotation(annotation);
		}

		return ret;

	}

	@Override
	public <A extends Annotation> boolean hasAnnotation(Class<? extends Item> type, Class<A> annotation) {
		return getAnnotation(type, annotation) != null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<? extends Item> type, Class<A> annotation) {
		return type.getAnnotation(annotation);
	}

	@Override
	public <A extends Annotation> boolean hasAnnotation(AccessibleObject member, Class<A> annotation) {
		return getAnnotation(member, annotation) != null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(AccessibleObject member, Class<A> annotation) {
		return member.getAnnotation(annotation);
	}

	@Override
	public List<Class<? extends Item>> getItemConcreteTypes(List<ModuleDefinition> moduleDefinitions) {
		List<Class<? extends Item>> itemTypes = new ArrayList<>();

		for (ModuleDefinition m : moduleDefinitions) {
			Reflections reflections = new Reflections(m.getModelPackagePath());
			Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ItemType.class);

			for (Class<?> clazz : annotated) {
				if (clazz.isAnnotationPresent(ItemType.class) && Item.class.isAssignableFrom(clazz)) {
					itemTypes.add((Class<? extends Item>) clazz);
				}
			}
		}

		return itemTypes;
	}

	/*
	 * *************************************************************************
	 * **************************** ITEM TYPE DEFINITIONS
	 * *************************************************************************
	 * *************************
	 */

	@Override
	public Class<? extends Item> getType(String typeCode) {
		// we fetch an actual instantiated item type copy and get the class from
		// there.
		Class<? extends Item> type = (Class<? extends Item>) getApplicationContext().getBean(typeCode, Item.class)
				.getClass();

		return type;
	}

	@Override
	public Map<String, ItemTypeDefinition> getItemTypeDefinitions() {
		String[] typeCodes = getApplicationContext().getBeanNamesForType(Item.class);

		Map<String, ItemTypeDefinition> alltypes = new HashMap<>();

		for (String typeCode : typeCodes) {
			ItemTypeDefinition def = getItemTypeDefinition(typeCode);

			if (def != null) {
				alltypes.put(def.typeCode, def);
			}
		}

		return alltypes;
	}

	public ItemTypeDefinition getItemTypeDefinition(String typeCode) {
		Class<? extends Item> itemType = getType(typeCode);
		ItemType typeAnnotation = getAnnotation(itemType, ItemType.class);

		ItemTypeDefinition def = null;

		if (typeAnnotation != null) {
			def = new ItemTypeDefinition(typeCode, itemType.getName(), itemType.getSimpleName(),
					itemType.getPackage().getName());
		}

		return def;
	}

	@Override
	public Map<String, ItemTypePropertyDefinition> getItemTypeProperties(String typeCode) {
		Class<? extends Item> type = getType(typeCode);

		return getItemTypeProperties(type);
	}

	@Override
	public Map<String, ItemTypePropertyDefinition> getItemTypeProperties(Class<? extends Item> itemType) {
		Map<String, ItemTypePropertyDefinition> propertyMembers = new HashMap<>();

		// add all the fields
		for (Field m : itemType.getFields()) {
			Property annotation = getAnnotation(m, Property.class);

			if (annotation != null) {
				ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(),
						m.getDeclaringClass().getName(), annotation.readable(), annotation.writable(),
						annotation.initial(), annotation.unique(), annotation.itemValueProvider());

				propertyMembers.put(m.getName(), def);
			}
		}

		// add all the getter methods
		for (Method m : itemType.getMethods()) {
			Property annotation = getAnnotation(m, Property.class);

			if (annotation != null && m.getReturnType() != Void.class) {
				String name = m.getName();

				if (StringUtils.startsWithIgnoreCase(name, "get")) {
					name = StringUtils.substring(name, 3);
				} else if (StringUtils.startsWithIgnoreCase(name, "get")) {
					name = StringUtils.substring(name, 2);
				}

				name = Introspector.decapitalize(name);

				ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(),
						m.getDeclaringClass().getName(), annotation.readable(), annotation.writable(),
						annotation.initial(), annotation.unique(), annotation.itemValueProvider());

				propertyMembers.put(name, def);
			}
		}

		return propertyMembers;
	}
}
