package at.spot.core.infrastructure.strategy.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.SerializationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import at.spot.core.infrastructure.serialization.jackson.ItemDeserializer;
import at.spot.core.infrastructure.serialization.jackson.ItemIgnorePropertiesMixIn;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.strategy.SerializationStrategy;
import at.spot.core.model.Item;
import at.spot.itemtype.core.user.User;

/**
 * Implements a serialization strategy from and to json format using Gson.
 */
@Service
public class DefaultJsonSerializationStrategy implements SerializationStrategy {

	@Resource
	protected TypeService typeService;

	protected ObjectMapper jacksonMapper;
	protected ObjectReader jacksonReader;
	protected ObjectWriter jacksonWriter;
	protected boolean serializeNulls = false;
	protected boolean excludeFieldsWithoutExposeAnnotation = true;

	@PostConstruct
	public void init() throws ClassNotFoundException {
		jacksonMapper = new ObjectMapper();
		jacksonMapper.addMixIn(Item.class, ItemIgnorePropertiesMixIn.class);
		// jacksonMapper.enableDefaultTyping();
		// jacksonMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		SimpleModule module = new SimpleModule("itemTypes");
		module.addDeserializer(User.class, new ItemDeserializer<>());
		jacksonMapper.registerModule(module);

		jacksonReader = jacksonMapper.reader();
		jacksonWriter = jacksonMapper.writer();
	}

	@Override
	public <T> String serialize(final T object) throws SerializationException {
		if (object == null) {
			return null;
		}

		try {
			return jacksonWriter.writeValueAsString(object);
		} catch (final JsonProcessingException e) {
			throw new SerializationException("Cannot serialize object", e);
		}
	}

	@Override
	public <T> T deserialize(final String serializedObject, final Class<T> type) throws SerializationException {
		try {
			return jacksonMapper.readValue(serializedObject, type);
		} catch (final Exception e) {
			throw new SerializationException("Cannot deserialize object", e);
		}
	}

	public void setSerializeNulls(final boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}
}
