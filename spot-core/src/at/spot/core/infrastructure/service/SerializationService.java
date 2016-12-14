package at.spot.core.infrastructure.service;

import at.spot.core.infrastructure.exception.DeserializationException;
import at.spot.core.infrastructure.exception.SerializationException;

public interface SerializationService {
	<T extends Object> String toJson(T object) throws SerializationException;

	<T extends Object> String toXml(T object) throws SerializationException;

	<T extends Object> String toBson(T object) throws SerializationException;

	<T extends Object> String toPropertyList(T object) throws SerializationException;

	<T extends Object> T fromJson(String value, Class<T> type) throws DeserializationException;

	<T extends Object> T fromXml(String value, Class<T> type) throws DeserializationException;

	<T extends Object> T fromBson(String value, Class<T> type) throws DeserializationException;

	<T extends Object> T fromPropertyList(String value, Class<T> type) throws DeserializationException;
}