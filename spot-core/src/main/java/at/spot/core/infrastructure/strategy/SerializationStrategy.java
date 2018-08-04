package at.spot.core.infrastructure.strategy;

import org.apache.commons.lang3.SerializationException;

public interface SerializationStrategy {
	/**
	 * Serializes the given object to a string representation
	 * 
	 * @param object
	 */
	<T extends Object> String serialize(T object) throws SerializationException;

	/**
	 * Deserializes the given string into the corresponding object.
	 * 
	 * @param serializedObject
	 */
	<T extends Object> T deserialize(String serializedObject, Class<T> type) throws SerializationException;
}
