package at.spot.core.infrastructure.support;

/**
 * Represents an item type definition.
 */
public class ItemTypeDefinition {

	protected final String typeCode;
	protected final String typeClass;
	protected final String typeName;
	protected final String packageName;

	public ItemTypeDefinition(String typeCode, String typeClass, String typeName, String packageName) {
		this.typeName = typeName;
		this.typeClass = typeClass;
		this.typeCode = typeCode;
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

}
