package at.spot.core.infrastructure.service.impl;

import org.springframework.stereotype.Service;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.spot.core.infrastructure.service.SerializationService;

@Service
public class DefaultSerializationService implements SerializationService {

	protected Gson gson;

	private DefaultSerializationService() {
		gson = Converters.registerAll(new GsonBuilder().serializeNulls()).create();
	}

	/**
	 * Users @Gson to serialize any object to a json string.
	 */
	@Override
	public <T> String toJson(final T object) {
		if (object == null) {
			return null;
		}

		return gson.toJson(object);
	}

	@Override
	public <T> String toXml(final T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String toBson(final T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String toPropertyList(final T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromJson(final String value, final Class<T> type) {
		return gson.fromJson(value, type);
	}

	@Override
	public JsonObject fromJson(final String value) {
		final JsonElement element = gson.fromJson(value, JsonElement.class);
		return element.getAsJsonObject();
	}

	@Override
	public <T> T fromXml(final String value, final Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromBson(final String value, final Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromPropertyList(final String value, final Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

}
