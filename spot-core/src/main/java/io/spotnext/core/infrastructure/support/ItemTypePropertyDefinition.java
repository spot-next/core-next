package io.spotnext.core.infrastructure.support;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an item type property definition
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemTypePropertyDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	protected final String name;
	protected final Class<?> returnType;
	protected final List<Class<?>> genericTypeArguments;
	protected final boolean isReadable;
	protected final boolean isWritable;
	protected final boolean isInitial;
	protected final boolean isUnique;
	protected final String itemValueProvider;
	protected final ItemTypePropertyRelationDefinition relationDefinition;

	/**
	 * <p>Constructor for ItemTypePropertyDefinition.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param returnType a {@link java.lang.Class} object.
	 * @param genericTypeArguments a {@link java.util.List} object.
	 * @param isReadable a boolean.
	 * @param isWritable a boolean.
	 * @param isInitial a boolean.
	 * @param isUnique a boolean.
	 * @param itemValueProvider a {@link java.lang.String} object.
	 * @param relationDefinition a {@link io.spotnext.core.infrastructure.support.ItemTypePropertyRelationDefinition} object.
	 */
	public ItemTypePropertyDefinition(final String name, final Class<?> returnType, final List<Class<?>> genericTypeArguments,
			final boolean isReadable, final boolean isWritable, final boolean isInitial, final boolean isUnique,
			final String itemValueProvider, final ItemTypePropertyRelationDefinition relationDefinition) {

		this.name = name;
		this.returnType = returnType;
		this.genericTypeArguments = genericTypeArguments;
		this.isReadable = isReadable;
		this.isWritable = isWritable;
		this.isInitial = isInitial;
		this.isUnique = isUnique;
		this.itemValueProvider = itemValueProvider;
		this.relationDefinition = relationDefinition;
	}

	/**
	 * <p>returnTypeAsString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String returnTypeAsString() {
		return returnType.getName();
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Getter for the field <code>returnType</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/**
	 * <p>Getter for the field <code>genericTypeArguments</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Class<?>> getGenericTypeArguments() {
		return genericTypeArguments;
	}

	/**
	 * <p>isReadable.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isReadable() {
		return isReadable;
	}

	/**
	 * <p>isWritable.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isWritable() {
		return isWritable;
	}

	/**
	 * <p>isInitial.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isInitial() {
		return isInitial;
	}

	/**
	 * <p>isUnique.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isUnique() {
		return isUnique;
	}

	/**
	 * <p>Getter for the field <code>itemValueProvider</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getItemValueProvider() {
		return itemValueProvider;
	}

	/**
	 * <p>Getter for the field <code>relationDefinition</code>.</p>
	 *
	 * @return a {@link io.spotnext.core.infrastructure.support.ItemTypePropertyRelationDefinition} object.
	 */
	public ItemTypePropertyRelationDefinition getRelationDefinition() {
		return relationDefinition;
	}
}
