package at.spot.core.infrastructure.type;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyDefinition {

	public final String name;
	public final Class<?> returnType;
	public final boolean isReadable;
	public final boolean isWritable;
	public final boolean isInitial;
	public final boolean isUnique;
	public final String itemValueProvider;

	public ItemTypePropertyDefinition(final String name, final Class<?> returnType, final boolean isReadable,
			final boolean isWritable, final boolean isInitial, final boolean isUnique, final String itemValueProvider) {

		this.name = name;
		this.returnType = returnType;
		this.isReadable = isReadable;
		this.isWritable = isWritable;
		this.isInitial = isInitial;
		this.isUnique = isUnique;
		this.itemValueProvider = itemValueProvider;
	}

	public String returnTypeAsString() {
		return returnType.getName();
	}
}
