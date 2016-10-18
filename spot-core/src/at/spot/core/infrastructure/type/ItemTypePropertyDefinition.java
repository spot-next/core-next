package at.spot.core.infrastructure.type;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyDefinition {

	public final String name;
	public final String returnType;
	public final boolean isReadable;
	public final boolean isWritable;
	public final boolean isInitial;
	public final boolean isUnique;
	public final String itemValueProvider;

	public ItemTypePropertyDefinition(String name, String returnType, boolean isReadable, boolean isWritable,
			boolean isInitial, boolean isUnique, String itemValueProvider) {

		this.name = name;
		this.returnType = returnType;
		this.isReadable = isReadable;
		this.isWritable = isWritable;
		this.isInitial = isInitial;
		this.isUnique = isUnique;
		this.itemValueProvider = itemValueProvider;
	}
}
