package io.spotnext.core.infrastructure.strategy.impl;

import java.lang.reflect.Modifier;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.SerializationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.serialization.jackson.ItemDeserializer;
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

/**
 * Implements a serialization strategy from and to json format using Gson.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultJsonSerializationStrategy extends AbstractService implements SerializationStrategy {

	@Resource
	private TypeService typeService;

	private ObjectMapper jacksonMapper;
	private ObjectWriter jacksonWriter;

	/**
	 * <p>
	 * init.
	 * </p>
	 *
	 * @throws java.lang.ClassNotFoundException if any.
	 */
	@PostConstruct
	public void init() throws ClassNotFoundException {
		jacksonMapper = new ObjectMapper();
		jacksonMapper.addMixIn(Item.class, ItemSerializationMixIn.class);
		jacksonMapper.addMixIn(ModelAndView.class, ModelAndViewMixIn.class);
		jacksonMapper.addMixIn(ModelAndView.class, QueryResultMixIn.class);

		jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jacksonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		jacksonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		// jacksonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd
		// hh:mm:ss"));

		// needed for localizedStrings, because otherwise the default locale
		// value would be returned instead of the real item
		jacksonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		jacksonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
		jacksonMapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);

		final TypeResolverBuilder<?> typeResolver = new ItemTypeResolverBuilder();
		typeResolver.init(JsonTypeInfo.Id.CUSTOM, new ItemTypeResolver());
		typeResolver.inclusion(JsonTypeInfo.As.PROPERTY);
		typeResolver.typeProperty("typeCode");
		typeResolver.typeIdVisibility(true);
		jacksonMapper.setDefaultTyping(typeResolver);

		final SimpleModule module = new SimpleModule();

		typeService.getItemTypeDefinitions().values().stream().forEach(t -> {
			try {
				final Class<? extends Item> type = typeService.getClassForTypeCode(t.getTypeCode());

				if (!Modifier.isAbstract(type.getModifiers())) {
					module.addDeserializer(type, new ItemDeserializer(type));
				}

				jacksonMapper.registerSubtypes(type);
			} catch (final UnknownTypeException e) {
				loggingService.warn(String.format("Could not load class for item type with code=%s", t.getTypeCode()));
			}
		});

		jacksonMapper.registerModule(module);
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
	public <T> T deserialize(final String serializedObject, final Class<T> type) throws SerializationException {
		try {
			return this.jacksonMapper.readValue(serializedObject, type);
		} catch (final Exception e) {
			throw new SerializationException("Cannot deserialize object: " + e.getMessage(), e);
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

}
