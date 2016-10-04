package at.spot.core.persistence.valueprovider;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

import at.spot.core.data.model.Item;

public interface ItemPropertyValueProvider<I extends Item, V> {

	/**
	 * Reads the given property.
	 * 
	 * @param item
	 * @param attributeName
	 * @return
	 * @throws NotReadablePropertyException
	 */
	V readValue(I item, String attributeName) throws NotReadablePropertyException;

	/**
	 * Writes the given value into the corresponding item.
	 * 
	 * @param item
	 * @param attributeName
	 * @param value
	 * @throws NotWritablePropertyException
	 */
	void writeValue(I item, String attributeName, V value) throws NotWritablePropertyException;

}
