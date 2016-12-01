package at.spot.core.infrastructure.service.impl;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.ModuleDefinition;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.SpringUtil;

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
		for (final Class<?> clazz : getItemConcreteTypes(moduleDefinitions)) {
			if (clazz.isAnnotationPresent(ItemType.class)) {
				registerType(clazz, "prototype");
			}
		}
	}

	protected void registerType(final Class<?> type, final String scope) {
		final ItemType ann = type.getAnnotation(ItemType.class);

		String beanName = type.getSimpleName();

		// use the annotated itemtype name, it should
		if (ann != null && StringUtils.isNotBlank(ann.typeCode())) {
			beanName = ann.typeCode();
		}

		SpringUtil.registerBean(getBeanFactory(), type, beanName, "prototype", null);

		loggingService.debug(String.format("Registering type: %s", type.getSimpleName()));
	}

	/*
	 * *************************************************************************
	 * ANNOTATIONS
	 * *************************************************************************
	 */

	@Override
	public List<Class<? extends Item>> getItemConcreteTypes(final List<ModuleDefinition> moduleDefinitions) {
		final List<Class<? extends Item>> itemTypes = new ArrayList<>();

		for (final ModuleDefinition m : moduleDefinitions) {
			final Reflections reflections = new Reflections(m.getModelPackagePath());
			final Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ItemType.class);

			for (final Class<?> clazz : annotated) {
				if (clazz.isAnnotationPresent(ItemType.class) && Item.class.isAssignableFrom(clazz)) {
					itemTypes.add((Class<? extends Item>) clazz);
				}
			}
		}

		return itemTypes;
	}

	/*
	 * *************************************************************************
	 * ITEM TYPE DEFINITIONS
	 * *************************************************************************
	 */

	@Override
	public Class<? extends Item> getType(final String typeCode) throws UnknownTypeException {
		// we fetch an actual instantiated item type copy and get the class from
		// there.

		Class<? extends Item> type = null;

		try {
			type = getApplicationContext().getBean(typeCode, Item.class).getClass();
		} catch (final BeansException e) {
			throw new UnknownTypeException(e);
		}

		if (type == null) {
			throw new UnknownTypeException("No item type found");
		}

		return type;
	}

	@Override
	public Map<String, ItemTypeDefinition> getItemTypeDefinitions() throws UnknownTypeException {
		final String[] typeCodes = getApplicationContext().getBeanNamesForType(Item.class);

		final Map<String, ItemTypeDefinition> alltypes = new HashMap<>();

		for (final String typeCode : typeCodes) {
			final ItemTypeDefinition def = getItemTypeDefinition(typeCode);

			if (def != null) {
				alltypes.put(def.typeCode, def);
			}
		}

		return alltypes;
	}

	@Override
	public ItemTypeDefinition getItemTypeDefinition(final String typeCode) throws UnknownTypeException {
		final Class<? extends Item> itemType = getType(typeCode);
		final ItemType typeAnnotation = ClassUtil.getAnnotation(itemType, ItemType.class);

		ItemTypeDefinition def = null;

		if (typeAnnotation != null) {
			def = new ItemTypeDefinition(typeCode, itemType.getName(), itemType.getSimpleName(),
					itemType.getPackage().getName());
		}

		return def;
	}

	@Override
	public Map<String, ItemTypePropertyDefinition> getItemTypeProperties(final String typeCode)
			throws UnknownTypeException {

		final Class<? extends Item> type = getType(typeCode);

		return getItemTypeProperties(type);
	}

	@Override
	public Map<String, ItemTypePropertyDefinition> getItemTypeProperties(final Class<? extends Item> itemType) {
		final Map<String, ItemTypePropertyDefinition> propertyMembers = new HashMap<>();

		// add all the fields
		for (final Field m : itemType.getFields()) {
			final Property annotation = ClassUtil.getAnnotation(m, Property.class);

			if (annotation != null) {
				final ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(), m.getType(),
						annotation.readable(), annotation.writable(), annotation.initial(), annotation.unique(),
						annotation.itemValueProvider());

				propertyMembers.put(m.getName(), def);
			}
		}

		// add all the getter methods
		for (final Method m : itemType.getMethods()) {
			final Property annotation = ClassUtil.getAnnotation(m, Property.class);

			if (annotation != null && m.getReturnType() != Void.class) {
				String name = m.getName();

				if (StringUtils.startsWithIgnoreCase(name, "get")) {
					name = StringUtils.substring(name, 3);
				} else if (StringUtils.startsWithIgnoreCase(name, "get")) {
					name = StringUtils.substring(name, 2);
				}

				name = Introspector.decapitalize(name);

				final ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(), m.getReturnType(),
						annotation.readable(), annotation.writable(), annotation.initial(), annotation.unique(),
						annotation.itemValueProvider());

				propertyMembers.put(name, def);
			}
		}

		return propertyMembers;
	}

	@Override
	public Map<String, ItemTypePropertyDefinition> getUniqueItemTypeProperties(final Class<? extends Item> itemType) {
		final Map<String, ItemTypePropertyDefinition> props = getItemTypeProperties(itemType);

		final Map<String, ItemTypePropertyDefinition> uniqueProps = new HashMap<>();

		for (final String k : props.keySet()) {
			final ItemTypePropertyDefinition v = props.get(k);
			if (v.isUnique) {
				uniqueProps.put(k, v);
			}
		}

		return null;
	}
}
