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
	public <T> String toJson(T object) {
		return gson.toJson(object);
	}

	@Override
	public <T> String toXml(T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String toBson(T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String toPropertyList(T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromJson(T object, Class<T> type) {
//		return gson.fromJson(type, object);
		return null;
	}

	@Override
	public <T> T fromXml(T object, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromBson(T object, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T fromPropertyList(T object, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

}
