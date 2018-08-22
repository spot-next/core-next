package io.spotnext.core.infrastructure.maven;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.spotnext.core.infrastructure.maven.xml.AtomicType;
import io.spotnext.core.infrastructure.maven.xml.BaseComplexType;
import io.spotnext.core.infrastructure.maven.xml.BaseType;
import io.spotnext.core.infrastructure.maven.xml.BeanType;
import io.spotnext.core.infrastructure.maven.xml.CollectionType;
import io.spotnext.core.infrastructure.maven.xml.EnumType;
import io.spotnext.core.infrastructure.maven.xml.ItemType;
import io.spotnext.core.infrastructure.maven.xml.MapType;
import io.spotnext.core.infrastructure.maven.xml.RelationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "types")
public class TypeDefinitions {
	final Map<String, AtomicType> atomicTypes = new HashMap<>();
	final Map<String, ItemType> itemTypes = new HashMap<>();
	final Map<String, CollectionType> collectionTypes = new HashMap<>();
	final Map<String, MapType> mapTypes = new HashMap<>();
	final Map<String, EnumType> enumTypes = new HashMap<>();
	final Map<String, BeanType> beanTypes = new HashMap<>();
	final Map<String, RelationType> relationTypes = new HashMap<>();

	public Map<String, ItemType> getItemTypes() {
		return itemTypes;
	}

	public Map<String, EnumType> getEnumTypes() {
		return enumTypes;
	}

	public Map<String, BeanType> getBeanTypes() {
		return beanTypes;
	}

	public Map<String, AtomicType> getAtomicTypes() {
		return atomicTypes;
	}

	public Map<String, CollectionType> getCollectionTypes() {
		return collectionTypes;
	}

	public Map<String, MapType> getMapTypes() {
		return mapTypes;
	}

	public Map<String, RelationType> getRelationTypes() {
		return relationTypes;
	}

	/**
	 * Looks up the {@link BaseType} with the given name.
	 * 
	 * @param name
	 * @return null if no matching type is found
	 */
	public BaseType getType(String name) {
		BaseType type = atomicTypes.get(name);

		if (type == null) {
			type = collectionTypes.get(name);
		}

		if (type == null) {
			type = mapTypes.get(name);
		}

		if (type == null) {
			type = getComplexType(name);
		}

		return type;
	}

	/**
	 * Looks up the {@link BaseComplexType} with the given name.
	 * 
	 * @param name
	 * @return null if no matching type is found
	 */
	public BaseComplexType getComplexType(String name) {
		BaseComplexType type = enumTypes.get(name);

		if (type == null) {
			type = beanTypes.get(name);
		}

		if (type == null) {
			type = itemTypes.get(name);
		}

		return type;
	}

}
