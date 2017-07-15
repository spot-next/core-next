package at.spot.core.infrastructure.service.impl;

import java.beans.Introspector;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyRelationDefinition;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;

/**
 * Provides functionality to manage the typesystem.
 */
@Service
public class DefaultTypeService extends AbstractService implements TypeService {

	@Autowired
	protected List<? extends Item> itemTypes;

	@PostConstruct
	protected void init() {
		final List<String> typeCodes = itemTypes.stream().map(i -> getTypeCode(i.getClass()))
				.collect(Collectors.toList());
		loggingService.debug(String.format("Registered item types: %s", StringUtils.join(typeCodes, ", ")));
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
			// type codes are always lowercase
			type = getApplicationContext().getBean(StringUtils.lowerCase(typeCode), Item.class).getClass();
		} catch (final Exception e) {
			throw new UnknownTypeException(e);
		}

		if (type == null) {
			throw new UnknownTypeException("No item type found");
		}

		return type;
	}

	@Override
	public <I extends Item> String getTypeCode(final Class<I> itemType) {
		final ItemType annotation = ClassUtil.getAnnotation(itemType, ItemType.class);

		return annotation.typeCode();
	}

	@Override
	public List<String> getAllSubTypesCodes(final String typeCode, final boolean includeSuperType)
			throws UnknownTypeException {

		final Class<? extends Item> superType = getType(typeCode);

		final Map<String, ? extends Item> allTypes = getApplicationContext().getBeansOfType(superType);

		final List<String> typeCodes = allTypes.entrySet().stream().map((e) -> getTypeCode(e.getValue().getClass()))
				.collect(Collectors.toList());

		if (!includeSuperType) {
			// typeCodes = typeCodes.stream().filter((t) ->
			// !StringUtils.equals(t, typeCodes)).collect(Collectors.toList());
			typeCodes.remove(typeCode);
		}

		return typeCodes;
	}

	@Override
	public Map<String, ItemTypeDefinition> getItemTypeDefinitions() throws UnknownTypeException {
		final String[] beanNames = getApplicationContext().getBeanNamesForType(Item.class);

		// final List<String> typeCodes =
		// Arrays.asList(beanIds).stream().map((i) -> {
		// final String[] aliases = getApplicationContext().getAliases(i);
		//
		// return aliases != null && aliases.length > 0 ? aliases[0] : null;
		// }).filter((i) -> {
		// return StringUtils.isNotBlank(i);
		// }).collect(Collectors.toList());

		final Map<String, ItemTypeDefinition> alltypes = new HashMap<>();

		for (final String typeBeanName : beanNames) {
			final String[] aliases = getApplicationContext().getAliases(typeBeanName);

			ItemTypeDefinition def = null;

			if (aliases.length > 0) {
				def = getItemTypeDefinition(aliases[0]);
			} else {
				def = getItemTypeDefinition(typeBeanName);
			}

			alltypes.put(def.typeCode, def);
		}

		return alltypes;
	}

	@Override
	public ItemTypeDefinition getItemTypeDefinition(final String typeCode) throws UnknownTypeException {
		final Class<? extends Item> itemType = getType(typeCode);

		ItemTypeDefinition def = null;

		def = new ItemTypeDefinition(typeCode, itemType.getName(), itemType.getSimpleName(),
				itemType.getPackage().getName());

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
		for (final Field m : ClassUtil.getFieldsWithAnnotation(itemType, Property.class)) {
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

		for (final Map.Entry<String, ItemTypePropertyDefinition> entry : props.entrySet()) {
			if (entry.getValue().isUnique) {
				uniqueProps.put(entry.getKey(), entry.getValue());
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
