package at.spot.core.infrastructure.service.impl;

import org.springframework.stereotype.Service;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.spot.core.infrastructure.service.SerializationService;

@Service
public class DefaultSerializationService implements SerializationService {

	Gson gson;

	private DefaultSerializationService() {
		gson = Converters.registerAll(new GsonBuilder()).create();
	}

	/**
	 * Users @Gson to serialize any object to a json string.
	 */
	@Override
	public <T> String toJson(final T object) {
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
	public <T> T fromJson(final T object, final Class<T> type) {
		// return gson.fromJson(type, object);
		return null;
	}

	@Override
	public <T> T fromXml(final T object, final Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromBson(final T object, final Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromPropertyList(final T object, final Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

}
