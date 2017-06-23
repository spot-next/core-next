package at.spot.core.infrastructure.support;

/**
 * Represents an item type definition.
 */
public class ItemTypeDefinition {

	public final String typeCode;
	public final String typeClass;
	public final String typeName;
	public final String packageName;

	public ItemTypeDefinition(String typeCode, String typeClass, String typeName, String packageName) {
		this.typeName = typeName;
		this.typeClass = typeClass;
		this.typeCode = typeCode;
		this.packageName = packageName;
	}
}
