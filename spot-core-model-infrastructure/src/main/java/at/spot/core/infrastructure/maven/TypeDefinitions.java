package at.spot.core.infrastructure.maven;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.type.MapType;

import at.spot.core.infrastructure.maven.xml.AtomicType;
import at.spot.core.infrastructure.maven.xml.ElementCollectionType;
import at.spot.core.infrastructure.maven.xml.EnumType;
import at.spot.core.infrastructure.maven.xml.ItemType;
import at.spot.core.infrastructure.maven.xml.RelationType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "types")
public class TypeDefinitions {
	final Map<String, AtomicType> atomicTypes = new HashMap<>();
	final Map<String, ItemType> itemTypes = new HashMap<>();
	final Map<String, ElementCollectionType> collectionTypes = new HashMap<>();
	final Map<String, MapType> mapTypes = new HashMap<>();
	final Map<String, EnumType> enumTypes = new HashMap<>();
	final Map<String, RelationType> relationTypes = new HashMap<>();

	public Map<String, ItemType> getItemTypes() {
		return itemTypes;
	}

	public Map<String, EnumType> getEnumTypes() {
		return enumTypes;
	}

	public Map<String, AtomicType> getAtomicTypes() {
		return atomicTypes;
	}

	public Map<String, ElementCollectionType> getCollectionTypes() {
		return collectionTypes;
	}

	public Map<String, MapType> getMapTypes() {
		return mapTypes;
	}

	public Map<String, RelationType> getRelationTypes() {
		return relationTypes;
	}

}
