package at.spot.core.infrastructure.support;

import java.io.Serializable;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	public final String name;
	public final Class<?> returnType;
	public final boolean isReadable;
	public final boolean isWritable;
	public final boolean isInitial;
	public final boolean isUnique;
	public final String itemValueProvider;
	public final ItemTypePropertyRelationDefinition relationDefinition;

	public ItemTypePropertyDefinition(final String name, final Class<?> returnType, final boolean isReadable,
			final boolean isWritable, final boolean isInitial, final boolean isUnique, final String itemValueProvider,
			final ItemTypePropertyRelationDefinition relationDefinition) {

		this.name = name;
		this.returnType = returnType;
		this.isReadable = isReadable;
		this.isWritable = isWritable;
		this.isInitial = isInitial;
		this.isUnique = isUnique;
		this.itemValueProvider = itemValueProvider;
		this.relationDefinition = relationDefinition;
	}

	public String returnTypeAsString() {
		return returnType.getName();
	}
}
