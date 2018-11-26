package io.spotnext.core.infrastructure.service.impl;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.strategy.SerializationStrategy;
import io.spotnext.itemtype.core.beans.SerializationConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;

/**
 * <p>
 * DefaultSerializationService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultSerializationService implements SerializationService {

	@Autowired
	protected SerializationStrategy jsonSerializationStrategy;

	@Autowired
	protected SerializationStrategy xmlSerializationStrategy;

	@Override
	public <T> String serialize(SerializationConfiguration configuration, T object) throws SerializationException {
		return apply(configuration.getFormat(), //
				(s) -> s.serialize(object), //
				() -> {
					throw new SerializationException("No serialization strategy found for data format " + configuration.getFormat());
				});
	}

	@Override
	public <T> T deserialize(SerializationConfiguration configuration, String value, Class<T> type) throws DeserializationException {
		return apply(configuration.getFormat(), //
				(s) -> s.deserialize(value, type), //
				() -> {
					throw new DeserializationException("No deserialization strategy found for data format " + configuration.getFormat());
				});
	}

	@Override
	public <T> T deserialize(SerializationConfiguration configuration, String value, T instanceToUpdate) throws DeserializationException {
		return apply(configuration.getFormat(), //
				(s) -> s.deserialize(value, instanceToUpdate), //
				() -> {
					throw new DeserializationException("No deserialization strategy found for data format " + configuration.getFormat());
				});
	}

	protected <T> T apply(DataFormat format, Function<SerializationStrategy, T> formatFound,
			Runnable formatNotFound) {

		SerializationStrategy strategy = null;

		if (DataFormat.JSON.equals(format)) {
			strategy = jsonSerializationStrategy;
		} else if (DataFormat.XML.equals(format)) {
			strategy = xmlSerializationStrategy;
		}

		if (strategy != null) {
			return formatFound.apply(strategy);
		} else {
			formatNotFound.run();
		}

		return null;
	}
}
