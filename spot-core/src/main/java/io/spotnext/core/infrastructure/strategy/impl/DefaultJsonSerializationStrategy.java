package io.spotnext.core.infrastructure.strategy.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements a serialization strategy from and to json format using Gson.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultJsonSerializationStrategy extends AbstractJacksonSerializationStrategy {
	@Override
	protected ObjectMapper createMapper() {
		return new ObjectMapper();
	}
}
