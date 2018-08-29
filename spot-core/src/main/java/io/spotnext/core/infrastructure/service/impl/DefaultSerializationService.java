package io.spotnext.core.infrastructure.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.strategy.SerializationStrategy;

/**
 * <p>DefaultSerializationService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultSerializationService implements SerializationService {

	@Resource
	protected SerializationStrategy jsonSerializationStrategy;

	@Resource
	protected SerializationStrategy xmlSerializationStrategy;

	/** {@inheritDoc} */
	@Override
	public <T> String toJson(final T object) throws SerializationException {
		return jsonSerializationStrategy.serialize(object);
	}

	/** {@inheritDoc} */
	@Override
	public <T> String toXml(final T object) throws SerializationException {
		return xmlSerializationStrategy.serialize(object);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T fromJson(final String value, final Class<T> type) throws DeserializationException {
		return jsonSerializationStrategy.deserialize(value, type);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T fromJson(final String value, final T instanceToUpdate) throws DeserializationException {
		return jsonSerializationStrategy.deserialize(value, instanceToUpdate);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T fromXml(final String value, final Class<T> type) throws DeserializationException {
		return xmlSerializationStrategy.deserialize(value, type);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T fromXml(final String value, final T instanceToUpdate) throws DeserializationException {
		return xmlSerializationStrategy.deserialize(value, instanceToUpdate);
	}

}
