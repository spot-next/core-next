package io.spotnext.core.infrastructure.service;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.itemtype.core.beans.SerializationConfiguration;

/**
 * A Service handling de-/serialization of objects. The qctual conversion is taking place in the
 * {@link io.spotnext.infrastructure.strategy.SerializationStrategy} implementations.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SerializationService {
	/**
	 * Serializes the given object into the format specified using
	 * {@link SerializationConfiguration#setFormat(io.spotnext.itemtype.core.enumeration.DataFormat)}.
	 *
	 * @param object a T object.
	 * @throws io.spotnext.infrastructure.exception.SerializationException
	 * @param <T> a T object.
	 * @return a {@link java.lang.String} object.
	 */
	<T> String serialize(SerializationConfiguration configuration, T object) throws SerializationException;

	/**
	 * Deserializes the given string into a new instance of the given type.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @param type  a {@link java.lang.Class} object.
	 * @throws io.spotnext.infrastructure.exception.DeserializationException
	 * @param <T> a T object.
	 * @return a T object.
	 */
	<T> T deserialize(SerializationConfiguration configuration, String value, Class<T> type) throws DeserializationException;

	/**
	 * Deserializes the given string into an existing object.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @throws io.spotnext.infrastructure.exception.DeserializationException
	 * @param instanceToUpdate a T object.
	 * @param                  <T> a T object.
	 * @return a T object.
	 */
	<T> T deserialize(SerializationConfiguration configuration, String value, T instanceToUpdate) throws DeserializationException;

}
