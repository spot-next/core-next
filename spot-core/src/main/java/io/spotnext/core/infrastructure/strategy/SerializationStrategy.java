package io.spotnext.core.infrastructure.strategy;

import org.apache.commons.lang3.SerializationException;

import io.spotnext.core.infrastructure.exception.DeserializationException;

/**
 * <p>SerializationStrategy interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SerializationStrategy {
	/**
	 * Serializes the given object to a string representation.
	 *
	 * @param object
	 *            the input object that should be serialized
	 * @return the string representation of the given object
	 * @throws org.apache.commons.lang3.SerializationException
	 * @param <T> a T object.
	 */
	<T extends Object> String serialize(T object) throws SerializationException;

	/**
	 * Deserializes the given string into an object instance of the given type.
	 *
	 * @param serializedObject
	 *            a string representation of the object to deserialize
	 * @param type
	 *            of the deserialized object
	 * @return the deserialized object instance
	 * @throws org.apache.commons.lang3.SerializationException
	 * @param <T> a T object.
	 */
	<T extends Object> T deserialize(String serializedObject, Class<T> type) throws DeserializationException;

	/**
	 * Deserializes the given string and then merges the date into the given
	 * object instance.
	 *
	 * @param serializedObject
	 *            a string representation of the object to deserialize
	 * @param instanceToUpdate
	 *            the object that should be updated by the given serialized
	 *            object string
	 * @return the input object, only updated with the data from the
	 *         deserialized object
	 * @throws org.apache.commons.lang3.SerializationException
	 * @param <T> a T object.
	 */
	<T extends Object> T deserialize(String serializedObject, T instanceToUpdate) throws SerializationException;
}
