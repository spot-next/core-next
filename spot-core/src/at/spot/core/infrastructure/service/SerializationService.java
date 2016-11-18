package at.spot.core.infrastructure.service;

import com.google.gson.JsonObject;

public interface SerializationService {
	<T extends Object> String toJson(T object);

	<T extends Object> String toXml(T object);

	<T extends Object> String toBson(T object);

	<T extends Object> String toPropertyList(T object);

	<T extends Object> T fromJson(String value, Class<T> type);

	JsonObject fromJson(final String value);

	<T extends Object> T fromXml(String value, Class<T> type);

	<T extends Object> T fromBson(String value, Class<T> type);

	<T extends Object> T fromPropertyList(String value, Class<T> type);
}