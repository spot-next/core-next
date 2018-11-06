package io.spotnext.core.infrastructure.strategy.impl;

import java.lang.reflect.Modifier;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.serialization.jackson.ItemDeserializer;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.infrastructure.type.Item;

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
		ObjectMapper jacksonMapper = new ObjectMapper();

		// needed for localizedStrings, because otherwise the default locale
		// value would be returned instead of the real item
		jacksonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		jacksonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
		jacksonMapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
		
		final SimpleModule module = new SimpleModule();

		typeService.getItemTypeDefinitions().values().stream().forEach(t -> {
			try {
				final Class<? extends Item> type = typeService.getClassForTypeCode(t.getTypeCode());

				if (!Modifier.isAbstract(type.getModifiers())) {
					module.addDeserializer(type, new ItemDeserializer(type));
				}

				jacksonMapper.registerSubtypes(type);
			} catch (final UnknownTypeException e) {
				Logger.warn(String.format("Could not load class for item type with code=%s", t.getTypeCode()));
			}
		});

		jacksonMapper.registerModule(module);

		return jacksonMapper;
	}
}
