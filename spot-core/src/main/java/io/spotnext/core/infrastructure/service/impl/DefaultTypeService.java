package io.spotnext.core.infrastructure.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.infrastructure.annotation.ItemType;
import io.spotnext.infrastructure.annotation.Property;
import io.spotnext.infrastructure.annotation.Relation;
import io.spotnext.infrastructure.constants.InfrastructureConstants;
import io.spotnext.infrastructure.maven.xml.Types;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;
import io.spotnext.infrastructure.type.ItemTypePropertyRelationDefinition;
import io.spotnext.support.util.ClassUtil;
import io.spotnext.support.util.FileUtils;
import io.spotnext.support.util.MiscUtil;
import io.spotnext.support.util.ValidationUtil;

/**
 * Provides functionality to manage the type system.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultTypeService extends AbstractService implements TypeService {

	@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	final protected Map<String, io.spotnext.infrastructure.maven.xml.ItemType> xmlItemTypeDefinitions = new HashMap<>();

	final protected Map<String, Class<? extends Item>> itemTypeClasses = new HashMap<>();
	final protected Map<String, ItemTypeDefinition> itemTypeDefinitions = new HashMap<>();

	@Autowired
	protected DefaultTypeService(List<? extends Item> itemTypes) throws JAXBException {
		loadMergedItemTypeDefinitions();

		final List<String> typeCodes = itemTypes.stream().map(i -> getTypeCodeForClass(i.getClass()))
				.collect(Collectors.toList());
		Logger.info(String.format("Registered item types: %s", StringUtils.join(typeCodes, ", ")));
	}

	@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
	protected void loadMergedItemTypeDefinitions() {
		final URL applicationRoot = Registry.getMainClass().getProtectionDomain().getCodeSource().getLocation();

		Logger.info(String.format("Detected application root path: %s", applicationRoot.toString()));

		InputStream mergedItemDef = null;

		if (applicationRoot.getProtocol().equals("jar")) {
			try {

				final JarFile jarFile = ((JarURLConnection) applicationRoot.openConnection()).getJarFile();
				mergedItemDef = FileUtils.readFileFromZipFile(jarFile,
						InfrastructureConstants.MERGED_ITEMTYPES_FILENAME);
			} catch (final IOException e) {
				throw new BeanCreationException("Cannot create read merged item type defintion files from jar.", e);
			}
		} else if (applicationRoot.getProtocol().equals("file") && applicationRoot.toString().endsWith(".jar")) {
			try {
				final JarFile jarFile = new JarFile(applicationRoot.getFile());
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

		Logger.debug(String.format("Loading merged item type definitions from %s", applicationRoot.toString()));

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

		Logger.debug(String.format("Loading %s enum types and %s item types.", typeDef.getEnum().size(),
				typeDef.getType().size()));

		for (final io.spotnext.infrastructure.maven.xml.ItemType t : typeDef.getType()) {
			xmlItemTypeDefinitions.put(t.getTypeCode(), t);

			try {
				itemTypeClasses.put(t.getTypeCode(), getItemClass(t.getPackage(), t.getName()));

				Class<? extends Item> itemTypeClass = itemTypeClasses.get(t.getTypeCode());

				itemTypeDefinitions.put(t.getTypeCode(), createItemTypeDefinition(t.getTypeCode(), t.getName(),
						t.getPackage(), getItemTypeProperties(itemTypeClass)));
			} catch (ClassNotFoundException e) {
				throw new BeanCreationException(
						String.format("Cannot instantiate class object for item type %s.", t.getTypeCode()), e);
			}
		}

		Logger.info(String.format("Type service initialized"));
	}

	/*
	 * *************************************************************************
	 * ITEM TYPE DEFINITIONS
	 * *************************************************************************
	 */

	/** {@inheritDoc} */
	@Override
	public Class<? extends Item> getClassForTypeCode(final String typeCode) throws UnknownTypeException {
		Class<? extends Item> type = itemTypeClasses.get(typeCode);

		if (type == null) {
			throw new UnknownTypeException(String.format("Type '%s' not found", typeCode));
		}

		return type;
	}

	protected Class<? extends Item> getItemClass(final String classPackage, final String className)
			throws ClassNotFoundException {

		return (Class<? extends Item>) Class.forName(String.format("%s.%s", classPackage, className));
	}

	/** {@inheritDoc} */
	@Override
	public <I extends Item> String getTypeCodeForClass(final Class<I> itemType) {
		final ItemType annotation = ClassUtil.getAnnotation(itemType, ItemType.class);

		if (annotation != null) {
			return annotation.typeCode();
		}

		Logger.error(String.format("%s has no item type annotation", itemType.getClass().getName()));

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, ItemTypeDefinition> getItemTypeDefinitions() {
		return MapUtils.unmodifiableMap(itemTypeDefinitions);
	}

	/** {@inheritDoc} */
	@Override
	public ItemTypeDefinition getItemTypeDefinition(final String typeCode) throws UnknownTypeException {
		ValidationUtil.validateNotEmpty("Type code cannot be empty", typeCode);

		ItemTypeDefinition itemType = itemTypeDefinitions.get(typeCode);

		if (itemType == null) {
			throw new UnknownTypeException(String.format("No type found for type code '%s'", typeCode));
		}

		return itemType;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, ItemTypePropertyDefinition> getItemTypeProperties(String typeCode) throws UnknownTypeException {
		return getItemTypeDefinition(typeCode).getProperties();
	}

	protected ItemTypeDefinition createItemTypeDefinition(final String typeCode, String className, String packageName,
			Map<String, ItemTypePropertyDefinition> properties) {

		return new ItemTypeDefinition(typeCode, packageName + "." + className, className, packageName, properties);
	}

	/**
	 * Returns a map of all the @Property annotated properties of the given item.
	 */
	protected Map<String, ItemTypePropertyDefinition> getItemTypeProperties(Class<? extends Item> itemType) {
		// TODO: needs reworking to get data from xml instead of class
		final Map<String, ItemTypePropertyDefinition> propertyMembers = new HashMap<>();

		// add all the fields
		for (final Field m : ClassUtil.getFieldsWithAnnotation(itemType, Property.class)) {
			final Property propertyAnn = ClassUtil.getAnnotation(m, Property.class);

			if (propertyAnn != null) {

				final Type[] genericArguments;
				Type genericType = m.getGenericType();

				if (genericType instanceof ParameterizedType) {
					genericArguments = ((ParameterizedType) genericType).getActualTypeArguments();
				} else if (genericType instanceof Class) {
					genericArguments = ((Class<?>) genericType).getTypeParameters();
				} else {
					genericArguments = new Type[0];
				}

				// convert generic type arguments to class objects

				List<Class<?>> genericTypeArguments = new ArrayList<>();

				for (Type t : genericArguments) {
					if (t instanceof ParameterizedType) {
						genericTypeArguments.add((Class<?>) ((ParameterizedType) t).getRawType());
					} else if (t instanceof Class) {
						genericTypeArguments.add((Class<?>) t);
					} else {
						Logger.warn(String.format("Unsupported generic argument type: %s", t.getClass()));
					}
				}

				final ItemTypePropertyDefinition def = new ItemTypePropertyDefinition(m.getName(), m.getType(),
						genericTypeArguments, propertyAnn.readable(), propertyAnn.writable(), propertyAnn.initial(),
						propertyAnn.unique(), propertyAnn.itemValueProvider(),
						getRelationDefinition(itemType, m, m.getName()));

				propertyMembers.put(m.getName(), def);
			}
		}

		// // add all the getter methods
		// for (final Method m : itemType.getMethods()) {
		// final Property propertyAnn = ClassUtil.getAnnotation(m, Property.class);
		//
		// if (propertyAnn != null && m.getReturnType() != Void.class) {
		// String name = m.getName();
		//
		// if (StringUtils.startsWithIgnoreCase(name, "get")) {
		// name = StringUtils.substring(name, 3);
		// } else if (StringUtils.startsWithIgnoreCase(name, "get")) {
		// name = StringUtils.substring(name, 2);
		// }
		//
		// name = Introspector.decapitalize(name);
		//
		// final ItemTypePropertyDefinition def = new
		// ItemTypePropertyDefinition(m.getName(), m.getReturnType(),
		// ((Class<?>) m.getGenericReturnType()).getTypeParameters(),
		// propertyAnn.readable(),
		// propertyAnn.writable(), propertyAnn.initial(), propertyAnn.unique(),
		// propertyAnn.itemValueProvider(), getRelationDefinition(itemType, m,
		// m.getName()));
		//
		// propertyMembers.put(name, def);
		// }
		// }

		return propertyMembers;
	}

	protected ItemTypePropertyRelationDefinition getRelationDefinition(final Class<? extends Item> itemType,
			final AccessibleObject member, final String memberName) {

		final Relation r = ClassUtil.getAnnotation(member, Relation.class);

		ItemTypePropertyRelationDefinition def = null;

		if (r != null) {
			def = new ItemTypePropertyRelationDefinition(r.type(), r.mappedTo());
		}

		return def;
	}

}
