package io.spotnext.core.infrastructure.service;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.core.infrastructure.strategy.SerializationStrategy;

/**
 * A Service handling de-/serialization of objects. The qctual conversion is
 * taking place in the {@link io.spotnext.infrastructure.strategy.SerializationStrategy} implementations.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SerializationService {
	/**
	 * Serializes the given object to JSON format.
	 *
	 * @param object a T object.
	 * @throws io.spotnext.infrastructure.exception.SerializationException
	 * @param <T> a T object.
	 * @return a {@link java.lang.String} object.
	 */
	<T> String toJson(T object) throws SerializationException;

	/**
	 * Serializes the given object to XML format.
	 *
	 * @param object a T object.
	 * @throws io.spotnext.infrastructure.exception.SerializationException
	 * @param <T> a T object.
	 * @return a {@link java.lang.String} object.
	 */
	<T> String toXml(T object) throws SerializationException;

	/**
	 * Deserializes JSON into an object of the given type.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @param type a {@link java.lang.Class} object.
	 * @throws io.spotnext.infrastructure.exception.DeserializationException
	 * @param <T> a T object.
	 * @return a T object.
	 */
	<T> T fromJson(String value, Class<T> type) throws DeserializationException;

	/**
	 * Deserializes JSON into an existing object.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @throws io.spotnext.infrastructure.exception.DeserializationException
	 * @param instanceToUpdate a T object.
	 * @param <T> a T object.
	 * @return a T object.
	 */
	<T> T fromJson(String value, T instanceToUpdate) throws DeserializationException;

	/**
	 * Deserializes XML into an object of the given type.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @param type a {@link java.lang.Class} object.
	 * @throws io.spotnext.infrastructure.exception.DeserializationException
	 * @param <T> a T object.
	 * @return a T object.
	 */
	<T> T fromXml(String value, Class<T> type) throws DeserializationException;

	/**
	 * Deserializes XML into an existing object.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @throws io.spotnext.infrastructure.exception.DeserializationException
	 * @param instanceToUpdate a T object.
	 * @param <T> a T object.
	 * @return a T object.
	 */
	<T> T fromXml(String value, T instanceToUpdate) throws DeserializationException;
}
