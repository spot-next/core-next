package at.spot.maven.mojo;

import java.util.HashMap;
import java.util.Map;

import at.spot.maven.xml.EnumType;
import at.spot.maven.xml.ItemType;

public class TypeDefinitions {
	final Map<String, ItemType> itemTypes = new HashMap<>();
	final Map<String, EnumType> enumTypes = new HashMap<>();

	public Map<String, ItemType> getItemTypes() {
		return itemTypes;
	}

	public Map<String, EnumType> getEnumTypes() {
		return enumTypes;
	}
}
