package io.spotnext.core.infrastructure.service;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.core.infrastructure.strategy.SerializationStrategy;

/**
 * A Service handling de-/serialization of objects. The qctual conversion is
 * taking place in the {@link SerializationStrategy} implementations.
 */
public interface SerializationService {
	/**
	 * Serializes the given object to JSON format.
	 * 
	 * @param object
	 * @throws SerializationException
	 */
	<T> String toJson(T object) throws SerializationException;

	/**
	 * Serializes the given object to XML format.
	 * 
	 * @param object
	 * @throws SerializationException
	 */
	<T> String toXml(T object) throws SerializationException;

	/**
	 * Deserializes JSON into an object of the given type.
	 * 
	 * @param value
	 * @param type
	 * @throws DeserializationException
	 */
	<T> T fromJson(String value, Class<T> type) throws DeserializationException;

	/**
	 * Deserializes JSON into an existing object.
	 * 
	 * @param value
	 * @param type
	 * @throws DeserializationException
	 */
	<T> T fromJson(String value, T instanceToUpdate) throws DeserializationException;

	/**
	 * Deserializes XML into an object of the given type.
	 * 
	 * @param value
	 * @param type
	 * @throws DeserializationException
	 */
	<T> T fromXml(String value, Class<T> type) throws DeserializationException;

	/**
	 * Deserializes XML into an existing object.
	 * 
	 * @param value
	 * @param type
	 * @throws DeserializationException
	 */
	<T> T fromXml(String value, T instanceToUpdate) throws DeserializationException;
}