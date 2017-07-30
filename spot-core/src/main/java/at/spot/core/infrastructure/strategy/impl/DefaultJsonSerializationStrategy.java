package at.spot.core.infrastructure.strategy.impl;

import org.apache.commons.lang3.SerializationException;
import org.springframework.stereotype.Service;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import at.spot.core.infrastructure.serialization.ClassSerializer;
import at.spot.core.infrastructure.serialization.GsonExclusionStrategy;
import at.spot.core.infrastructure.strategy.SerializationStrategy;

/**
 * Implements a serialization strategy from and to json format using Gson.
 */
@Service
public class DefaultJsonSerializationStrategy implements SerializationStrategy {

	protected Gson gson;

	protected boolean serializeNulls = false;
	protected boolean excludeFieldsWithoutExposeAnnotation = true;

	private DefaultJsonSerializationStrategy() {
		final GsonBuilder builder = new GsonBuilder();

		if (serializeNulls) {
			builder.serializeNulls();
		}

		builder.setExclusionStrategies(new GsonExclusionStrategy(excludeFieldsWithoutExposeAnnotation));
		builder.registerTypeAdapter(Class.class, new ClassSerializer());

		// register helper builders for datetimes etc.
		gson = Converters.registerAll(builder).create();
	}

	@Override
	public <T> String serialize(final T object) throws SerializationException {
		if (object == null) {
			return null;
		}

		return gson.toJson(object);
	}

	@Override
	public <T> T deserialize(final String serializedObject, final Class<T> type) throws SerializationException {
		try {
			return gson.fromJson(serializedObject, type);
		} catch (final JsonSyntaxException e) {
			throw new SerializationException("Cannot deserialize object", e);
		}
	}

	public void setSerializeNulls(final boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}
}
