package io.spotnext.core.infrastructure.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.strategy.SerializationStrategy;

@Service
public class DefaultSerializationService implements SerializationService {

	@Resource
	protected SerializationStrategy jsonSerializationStrategy;

	@Resource
	protected SerializationStrategy xmlSerializationStrategy;

	/**
	 * Users @Gson to serialize any object to a json string.
	 */
	@Override
	public <T> String toJson(final T object) throws SerializationException {
		return jsonSerializationStrategy.serialize(object);
	}

	@Override
	public <T> String toXml(final T object) throws SerializationException {
		return xmlSerializationStrategy.serialize(object);
	}

	@Override
	public <T> T fromJson(final String value, final Class<T> type) throws DeserializationException {
		return jsonSerializationStrategy.deserialize(value, type);
	}

	@Override
	public <T> T fromJson(final String value, final T instanceToUpdate) throws DeserializationException {
		return jsonSerializationStrategy.deserialize(value, instanceToUpdate);
	}

	@Override
	public <T> T fromXml(final String value, final Class<T> type) throws DeserializationException {
		return xmlSerializationStrategy.deserialize(value, type);
	}

	@Override
	public <T> T fromXml(final String value, final T instanceToUpdate) throws DeserializationException {
		return xmlSerializationStrategy.deserialize(value, instanceToUpdate);
	}

}
