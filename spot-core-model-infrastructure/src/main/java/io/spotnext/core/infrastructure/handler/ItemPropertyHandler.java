package io.spotnext.core.infrastructure.handler;

import io.spotnext.core.types.Item;

/**
 * Handles property access to {@link Item} objects.
 */
public interface ItemPropertyHandler {
	/**
	 * @see #getProperty(Item, String, Class)
	 * 
	 * @param <T> the item type
	 * @param item from which the property should be fetched
	 * @param propertyName of the property to fetch
	 * @return the property value for the given item (can be null).
	 */
	<T extends Item> Object getProperty(T item, String propertyName);

	/**
	 * @param <T> the item type
	 * @param <R> the return type
	 * @param item from which the property should be fetched
	 * @param propertyName of the property to fetch
	 * @param returnType the return type
	 * @return the property value for the given item (can be null).
	 */
	<T extends Item, R> R getProperty(T item, String propertyName, Class<R> returnType);

	/**
	 * Sets a property for the given item.
	 * 
	 * @param <T> the item type
	 * @param item  for  which the given value should be set
	 * @param propertyName of the property for which the given value should be set
	 * @param value to set to the given property
	 */
	<T extends Item> void setProperty(T item, String propertyName, Object value);
}
