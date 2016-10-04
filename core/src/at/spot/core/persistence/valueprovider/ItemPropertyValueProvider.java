package at.spot.core.persistence.valueprovider;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

import at.spot.core.data.model.Item;

public interface ItemPropertyValueProvider {

	/**
	 * Reads the property
	 * 
	 * @param item
	 * @return
	 * @throws NotReadablePropertyException
	 */
	<T, I extends Item> T readValue(I item, String attributeName) throws NotReadablePropertyException;

	/**
	 * Writes the given value into the corresponding item.
	 * 
	 * @param item
	 * @param value
	 * @throws NotWritablePropertyException
	 */
	<T, I extends Item> void writeValue(I item, String attributeName, T value) throws NotWritablePropertyException;
}
