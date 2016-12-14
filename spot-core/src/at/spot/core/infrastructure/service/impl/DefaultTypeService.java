package at.spot.core.infrastructure.service.impl;

import java.beans.Introspector;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.spring.support.Registry;
import at.spot.core.infrastructure.type.ItemTypeDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.type.ItemTypePropertyRelationDefinition;
import at.spot.core.infrastructure.type.ModuleDefinition;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.SpringUtil;
import at.spot.core.support.util.SpringUtil.BeanScope;

/**
 * Provides functionality to manage the typesystem.
 *
 */
@Service
public class DefaultTypeService extends AbstractService implements TypeService {

	// @Autowired
	// protected List<ModuleDefinition> moduleDefinitions;

	@Autowired
	protected LoggingService loggingService;

	/*
	 * *************************************************************************
	 * **************************** TYPE SYSTEM INIT ***************************
	 * *************************************************************************
	 */

	@Override
	public void registerTypes() {
		final Map<String, ModuleDefinition> moduleDefinitions = Registry.getApplicationContext()
				.getBeansOfType(ModuleDefinition.class);

		for (final Class<?> clazz : getItemConcreteTypes(moduleDefinitions.values())) {
			if (clazz.isAnnotationPresent(ItemType.class)) {
				registerType(clazz, "prototype");
			}
		}
	}

	protected void registerType(final Class<?> type, final String scope) {
		final ItemType ann = type.getAnnotation(ItemType.class);

		final String alias = getTypeCode((Class<? extends Item>) type);
		SpringUtil.registerBean(getBeanFactory(), type, type.getSimpleName(), alias, BeanScope.prototype, null, false);

		loggingService.debug(String.format("Registering type: %s", alias));
	}

	/*
	 * *************************************************************************
	 * ANNOTATIONS
	 * *************************************************************************
	 */

	@Override
	public List<Class<? extends Item>> getItemConcreteTypes(final Collection<ModuleDefinition> moduleDefinitions) {
		final List<Class<? extends Item>> itemTypes = new ArrayList<>();

		for (final ModuleDefinition m : moduleDefinitions) {
			final Reflections reflections = new Reflections(m.getModelPackagePaths());
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
			type = getApplicationContext().getBean(typeCode.toLowerCase(), Item.class).getClass();
		} catch (final Exception e) {
			throw new UnknownTypeException(e);
		}

		if (type == null) {
			throw new UnknownTypeException("No item type found");
		}

		return type;
	}

	@Override
	public String getTypeCode(final Class<? extends Item> itemType) {
		final ItemType ann = ClassUtil.getAnnotation(itemType, ItemType.class);

		String typeCode = ann.typeCode();

		if (StringUtils.isBlank(typeCode)) {
			typeCode = itemType.getSimpleName();
		}

		return typeCode.toLowerCase();
	}

	@Override
	public Map<String, ItemTypeDefinition> getItemTypeDefinitions() throws UnknownTypeException {
		final String[] beanIds = getApplicationContext().getBeanNamesForType(Item.class);

		final List<String> typeCodes = Arrays.asList(beanIds).stream().map((i) -> {
			final String[] aliases = getApplicationContext().getAliases(i);

			return (aliases != null && aliases.length > 0) ? aliases[0] : null;
		}).filter((i) -> {
			return StringUtils.isNotBlank(i);
		}).collect(Collectors.toList());

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
			final Property propertyAnn = ClassUtil.getAnnotation(m, Property.class);

			if (propertyAnn != null) {

				final ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(), m.getType(),
						propertyAnn.readable(), propertyAnn.writable(), propertyAnn.initial(), propertyAnn.unique(),
						propertyAnn.itemValueProvider(), getRelationDefinition(itemType, m, m.getName()));

				propertyMembers.put(m.getName(), def);
			}
		}

		// add all the getter methods
		for (final Method m : itemType.getMethods()) {
			final Property propertyAnn = ClassUtil.getAnnotation(m, Property.class);

			if (propertyAnn != null && m.getReturnType() != Void.class) {
				String name = m.getName();

				if (StringUtils.startsWithIgnoreCase(name, "get")) {
					name = StringUtils.substring(name, 3);
				} else if (StringUtils.startsWithIgnoreCase(name, "get")) {
					name = StringUtils.substring(name, 2);
				}

				name = Introspector.decapitalize(name);

				final ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(), m.getReturnType(),
						propertyAnn.readable(), propertyAnn.writable(), propertyAnn.initial(), propertyAnn.unique(),
						propertyAnn.itemValueProvider(), getRelationDefinition(itemType, m, m.getName()));

				propertyMembers.put(name, def);
			}
		}

		return propertyMembers;
	}

	protected ItemTypePropertyRelationDefinition getRelationDefinition(final Class<? extends Item> itemType,
			final AccessibleObject member, final String memberName) {

		final Relation r = ClassUtil.getAnnotation(member, Relation.class);

		ItemTypePropertyRelationDefinition def = null;

		if (r != null) {
			def = new ItemTypePropertyRelationDefinition(r.type(), r.referencedType(), r.mappedTo());
		}

		return def;
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

		return uniqueProps;
	}

	@Override
	public boolean isPropertyUnique(final Class<? extends Item> type, final String propertyName) {
		final ItemTypePropertyDefinition def = getUniqueItemTypeProperties(type).get(propertyName);

		if (def != null) {
			return def.isUnique;
		}

		return false;
	}
}
