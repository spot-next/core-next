package io.spotnext.core.infrastructure.resolver.impex.impl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.resolver.impex.ImpexValueResolver;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;
import io.spotnext.core.types.Localizable;

@Service
public class PrimitiveValueResolver implements ImpexValueResolver {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> T resolve(final String value, final Class<T> type, final List<Class<?>> genericArguments,
			final ColumnDefinition columnDefinition) throws ValueResolverException {

		if (StringUtils.isBlank(value)) {
			return null;
		}

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
		} catch (final IOException e) {
			throw new ValueResolverException(e);
		}
	}

	private boolean isBoolean(final String value) {
		return BooleanUtils.toBooleanObject(value) != null;
	}

	private Boolean toBoolean(final String value) {
		return BooleanUtils.toBooleanObject(value);
	}

	private Number toNumber(final String value) {
		return NumberUtils.createNumber(value);
	}
}
