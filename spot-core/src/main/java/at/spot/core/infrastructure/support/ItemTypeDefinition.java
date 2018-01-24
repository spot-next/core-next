package at.spot.core.infrastructure.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

/**
 * Represents an item type definition.
 */
public class ItemTypeDefinition {

	protected final String typeCode;
	protected final String typeClass;
	protected final String typeName;
	protected final String packageName;
	protected final Map<String, ItemTypePropertyDefinition> properties = new HashMap<String, ItemTypePropertyDefinition>();

	public ItemTypeDefinition(String typeCode, String typeClass, String typeName, String packageName,
			Map<String, ItemTypePropertyDefinition> properties) {

		this.typeCode = typeCode;
		this.typeClass = typeClass;
		this.typeName = typeName;
		this.packageName = packageName;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public String getTypeClass() {
		return typeClass;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getPackageName() {
		return packageName;
	}

	public Map<String, ItemTypePropertyDefinition> getProperties() {
		return MapUtils.unmodifiableMap(properties);
	}
}
