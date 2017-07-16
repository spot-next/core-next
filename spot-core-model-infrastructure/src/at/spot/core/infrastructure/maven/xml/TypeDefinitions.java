package at.spot.core.infrastructure.maven.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "types")
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
