package io.spotnext.core.infrastructure.strategy.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.SerializationException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.serialization.jackson.ItemSerializationMixIn;
import io.spotnext.core.infrastructure.serialization.jackson.ItemTypeResolver;
import io.spotnext.core.infrastructure.serialization.jackson.ItemTypeResolverBuilder;
import io.spotnext.core.infrastructure.serialization.jackson.ModelAndViewMixIn;
import io.spotnext.core.infrastructure.serialization.jackson.QueryResultMixIn;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.strategy.SerializationStrategy;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.infrastructure.type.Item;
import spark.ModelAndView;

public abstract class AbstractJacksonSerializationStrategy extends AbstractService implements SerializationStrategy {

	@Resource
	protected TypeService typeService;

	private ObjectMapper jacksonMapper;
	private ObjectWriter jacksonWriter;

	protected abstract ObjectMapper createMapper();

	/**
	 * <p>
	 * init.
	 * </p>
	 *
	 * @throws java.lang.ClassNotFoundException if any.
	 */
	@PostConstruct
	public void init() throws ClassNotFoundException {
		jacksonMapper = createMapper();

		jacksonMapper.addMixIn(Item.class, ItemSerializationMixIn.class);
		jacksonMapper.addMixIn(ModelAndView.class, ModelAndViewMixIn.class);
		jacksonMapper.addMixIn(ModelAndView.class, QueryResultMixIn.class);

		jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jacksonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		jacksonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		// jacksonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd
		// hh:mm:ss"));

		final TypeResolverBuilder<?> typeResolver = new ItemTypeResolverBuilder();
		typeResolver.init(JsonTypeInfo.Id.CUSTOM, new ItemTypeResolver());
		typeResolver.inclusion(JsonTypeInfo.As.PROPERTY);
		typeResolver.typeProperty("typeCode");
		typeResolver.typeIdVisibility(true);
		jacksonMapper.setDefaultTyping(typeResolver);

		jacksonMapper.registerModule(new Jdk8Module());
		jacksonMapper.registerModule(new JavaTimeModule());

		this.jacksonWriter = jacksonMapper.writer();
	}

	/** {@inheritDoc} */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Override
	public <T> String serialize(final T object) throws SerializationException {
		if (object == null) {
			return null;
		}

		try {
			return this.jacksonWriter.writeValueAsString(object);
		} catch (final JsonProcessingException e) {
			throw new SerializationException("Cannot serialize object: " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T> T deserialize(final String serializedObject, final Class<T> type) throws DeserializationException {
		try {
			return this.jacksonMapper.readValue(serializedObject, type);
		} catch (final Exception e) {
			throw new DeserializationException("Cannot deserialize object: " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Override
	public <T> T deserialize(final String serializedObject, final T instanceToUpdate) throws SerializationException {
		try {
			return this.jacksonMapper.readerForUpdating(instanceToUpdate).readValue(serializedObject);
		} catch (final Exception e) {
			throw new SerializationException("Cannot deserialize object: " + e.getMessage(), e);
		}
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}
}
