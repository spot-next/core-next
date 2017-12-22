package at.spot.core.infrastructure.support;

import java.io.Serializable;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	protected final String name;
	protected final Class<?> returnType;
	protected final boolean isReadable;
	protected final boolean isWritable;
	protected final boolean isInitial;
	protected final boolean isUnique;
	protected final String itemValueProvider;
	protected final ItemTypePropertyRelationDefinition relationDefinition;

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

	public String getName() {
		return name;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public boolean isReadable() {
		return isReadable;
	}

	public boolean isWritable() {
		return isWritable;
	}

	public boolean isInitial() {
		return isInitial;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public String getItemValueProvider() {
		return itemValueProvider;
	}

	public ItemTypePropertyRelationDefinition getRelationDefinition() {
		return relationDefinition;
	}
}
