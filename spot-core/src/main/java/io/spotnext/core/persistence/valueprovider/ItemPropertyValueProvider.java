package io.spotnext.core.persistence.valueprovider;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

import io.spotnext.core.types.Item;

/**
 * <p>ItemPropertyValueProvider interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ItemPropertyValueProvider<I extends Item, V> {

	/**
	 * Reads the given property.
	 *
	 * @param item a I object.
	 * @param attributeName a {@link java.lang.String} object.
	 * @throws org.springframework.beans.NotReadablePropertyException
	 * @return a V object.
	 */
	V readValue(I item, String attributeName) throws NotReadablePropertyException;

	/**
	 * Writes the given value into the corresponding item.
	 *
	 * @param item a I object.
	 * @param attributeName a {@link java.lang.String} object.
	 * @param value a V object.
	 * @throws org.springframework.beans.NotWritablePropertyException
	 */
	void writeValue(I item, String attributeName, V value) throws NotWritablePropertyException;

}
