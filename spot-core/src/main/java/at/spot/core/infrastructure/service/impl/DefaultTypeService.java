package at.spot.core.infrastructure.service.impl;

import java.beans.Introspector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.constants.InfrastructureConstants;
import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.maven.xml.Types;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.ItemTypePropertyRelationDefinition;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.FileUtils;
import at.spot.core.support.util.MiscUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides functionality to manage the type system.
 */
@Service
public class DefaultTypeService extends AbstractService implements TypeService {

	@Autowired
	protected List<? extends Item> itemTypes;

	@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	protected Map<String, at.spot.core.infrastructure.maven.xml.ItemType> itemTypeDefinition;

	@PostConstruct
	protected void init() throws JAXBException {
		loadMergedItemTypeDefinitions();

		final List<String> typeCodes = itemTypes.stream().map(i -> getTypeCode(i.getClass()))
				.collect(Collectors.toList());
		loggingService.info(String.format("Registered item types: %s", StringUtils.join(typeCodes, ", ")));
	}

	@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
	protected void loadMergedItemTypeDefinitions() {
		itemTypeDefinition = new HashMap<>();

		final URL applicationRoot = Registry.getMainClass().getProtectionDomain().getCodeSource().getLocation();

		loggingService.info(String.format("Detected application root path: %s", applicationRoot.toString()));

		InputStream mergedItemDef = null;

		if (applicationRoot.getProtocol().equals("jar")) {
			try {

				final JarFile jarFile = ((JarURLConnection) applicationRoot.openConnection()).getJarFile();
				mergedItemDef = FileUtils.readFileFromZipFile(jarFile,
						InfrastructureConstants.MERGED_ITEMTYPES_FILENAME);
			} catch (final IOException e) {
				throw new BeanCreationException("Cannot create read merged item type defintion files from jar.", e);
			}
		} else {
			try {
				final File applicationRootFile = java.nio.file.Paths.get(applicationRoot.toURI()).toFile();

				mergedItemDef = new FileInputStream(
						new File(applicationRootFile, InfrastructureConstants.MERGED_ITEMTYPES_FILENAME));
			} catch (final FileNotFoundException | URISyntaxException e) {
				throw new BeanCreationException("Cannot create read merged item type defintion files from path.", e);
			}
		}

		loggingService.debug(String.format("Loading merged item type definitions from %s", applicationRoot.toString()));

		Types typeDef = null;

		try {
			final JAXBContext context = JAXBContext.newInstance(Types.class);
			final Unmarshaller jaxb = context.createUnmarshaller();
			typeDef = (Types) jaxb.unmarshal(mergedItemDef);
		} catch (final JAXBException e) {
			throw new BeanCreationException("Cannot create read merged item type defintion files.", e);
		} finally {
			MiscUtil.closeQuietly(mergedItemDef);
		}

		loggingService.debug(String.format("Loading %s enum types and %s item types.", typeDef.getEnum().size(),
				typeDef.getType().size()));

		for (final at.spot.core.infrastructure.maven.xml.ItemType t : typeDef.getType()) {
			itemTypeDefinition.put(t.getTypeCode(), t);
		}
	}

	/*
	 * *************************************************************************
	 * ITEM TYPE DEFINITIONS
	 * *************************************************************************
	 */

	@Override
	public Class<? extends Item> getType(final String typeCode) throws UnknownTypeException {
		final at.spot.core.infrastructure.maven.xml.ItemType typeDef = itemTypeDefinition
				.get(StringUtils.lowerCase(typeCode));

		Class<? extends Item> type = null;

		if (typeDef != null) {
			try {
				type = getItemClass(typeDef.getPackage(), typeDef.getName());
			} catch (final ClassNotFoundException e) {
				loggingService.error("Item type definition is not of correct java type.");
			}
		}

		if (type == null) {
			throw new UnknownTypeException(String.format("Type %s not found", typeCode));
		}

		return type;
	}

	protected Class<? extends Item> getItemClass(final String classPackage, final String className)
			throws ClassNotFoundException {

		return (Class<? extends Item>) Class.forName(String.format("%s.%s", classPackage, className));
	}

	@Override
	public <I extends Item> String getTypeCode(final Class<I> itemType) {
		final ItemType annotation = ClassUtil.getAnnotation(itemType, ItemType.class);

		return annotation.typeCode();
	}

	@Override
	public List<Class<? extends Item>> getAllSubTypes(final Class<? extends Item> type, final boolean includeSuperType)
			throws UnknownTypeException {

		final List<Class<? extends Item>> types = new ArrayList<>();

		final at.spot.core.infrastructure.maven.xml.ItemType typeDef = itemTypeDefinition.get(getTypeCode(type));

		if (typeDef != null) {
			final List<at.spot.core.infrastructure.maven.xml.ItemType> typesDefs = new ArrayList<>();

			typesDefs.add(typeDef);

			for (int i = 0; i < typesDefs.size(); i++) {
				final at.spot.core.infrastructure.maven.xml.ItemType tempTypeDef = typesDefs.get(i);

				typesDefs.addAll(itemTypeDefinition.values().stream()
						.filter(d -> StringUtils.equals(d.getExtends(), tempTypeDef.getName()))
						.collect(Collectors.toList()));
			}

			if (!includeSuperType) {
				typesDefs.remove(typeDef);
			}

			for (final at.spot.core.infrastructure.maven.xml.ItemType t : typesDefs) {
				try {
					types.add(getItemClass(t.getPackage(), t.getName()));
				} catch (final ClassNotFoundException e) {
					throw new UnknownTypeException(String.format("Sub type %s not a valid item type.", t.getName()));
				}
			}

		} else {
			throw new UnknownTypeException(String.format("Type %s not found", type.getName()));
		}

		return types;
	}

	// @Override
	// public List<String> getAllSubTypesCodes(final String typeCode, final
	// boolean includeSuperType)
	// throws UnknownTypeException {
	//
	// final List<String> typeCodes = new ArrayList<>();
	//
	// final at.spot.core.infrastructure.maven.xml.ItemType typeDef =
	// itemTypeDefinition.getItemTypes().get(typeCode);
	//
	// if (typeDef != null) {
	// typeCodes.add(typeCode);
	//
	// for (int i = 0; i < typeCodes.size(); i++) {
	// final String tempTypeCode = typeCodes.get(i);
	//
	// typeCodes.addAll(itemTypeDefinition.getItemTypes().values().stream()
	// .filter(d -> StringUtils.equals(d.getExtends(), tempTypeCode)).map(d ->
	// d.getTypeCode())
	// .collect(Collectors.toList()));
	// }
	//
	// if (!includeSuperType) {
	// typeCodes.remove(typeCode);
	// }
	//
	// } else {
	// throw new UnknownTypeException(String.format("Type %s not found",
	// typeCode));
	// }
	//
	// return typeCodes;
	// }

	@Override
	public Map<String, ItemTypeDefinition> getItemTypeDefinitions() throws UnknownTypeException {
		final Map<String, ItemTypeDefinition> alltypes = new HashMap<>();

		for (final Map.Entry<String, at.spot.core.infrastructure.maven.xml.ItemType> typeDef : itemTypeDefinition
				.entrySet()) {

			final ItemTypeDefinition def = getItemTypeDefinition(typeDef.getValue().getTypeCode());
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
