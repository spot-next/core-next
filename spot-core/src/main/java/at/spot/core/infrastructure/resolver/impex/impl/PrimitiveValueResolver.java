package at.spot.core.infrastructure.resolver.impex.impl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.spot.core.infrastructure.exception.ValueResolverException;
import at.spot.core.infrastructure.resolver.impex.ImpexValueResolver;
import at.spot.core.infrastructure.support.impex.ColumnDefinition;
import at.spot.core.types.Localizable;

@Service
public class PrimitiveValueResolver implements ImpexValueResolver {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> T resolve(String value, Class<T> type, List<Class<?>> genericArguments,
			ColumnDefinition columnDefinition) throws ValueResolverException {
		try {
			if (Localizable.class.isAssignableFrom(type)) {
				Class<?> genericType = null;

				final Optional<ParameterizedType> localizableType = Stream.of(type.getGenericInterfaces()) //
						.filter(i -> i instanceof ParameterizedType) //
						.map(i -> (ParameterizedType) i) //
						.filter(i -> Localizable.class.isAssignableFrom((Class<?>) i.getRawType())).findFirst();

				if (localizableType.isPresent()) {
					if (ArrayUtils.isNotEmpty(localizableType.get().getActualTypeArguments())) {
						genericType = (Class<?>) localizableType.get().getActualTypeArguments()[0];
					}
				} else {
					throw new ValueResolverException(
							String.format("Cannot resolve generic value of localizable for %s.%s", type.getSimpleName(),
									columnDefinition.getPropertyName()));
				}

				return (T) resolve(value, genericType, genericArguments, columnDefinition);
			}
			if (type.isAssignableFrom(value.getClass())) {
				return (T) value;
			} else if (type.isAssignableFrom(Boolean.class) && isBoolean(value)) {
				return (T) toBoolean(value);
			} else if (type.isAssignableFrom(Number.class) && NumberUtils.isCreatable(value)) {
				return (T) toNumber(value);
			} else {

				// shortcut for locales
				if (Locale.class.isAssignableFrom(type)) {
					return (T) Locale.forLanguageTag(value);
				} else {
					return mapper.readValue(value, type);
				}
			}
		} catch (IOException e) {
			throw new ValueResolverException(e);
		}
	}

	private boolean isBoolean(String value) {
		return BooleanUtils.toBooleanObject(value) != null;
	}

	private Boolean toBoolean(String value) {
		return BooleanUtils.toBooleanObject(value);
	}

	private Number toNumber(String value) {
		return NumberUtils.createNumber(value);
	}
}
