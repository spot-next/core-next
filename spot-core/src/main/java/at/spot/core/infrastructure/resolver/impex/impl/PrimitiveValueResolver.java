package at.spot.core.infrastructure.resolver.impex.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.spot.core.infrastructure.exception.ValueResolverException;
import at.spot.core.infrastructure.resolver.impex.ImpexValueResolver;
import at.spot.core.infrastructure.support.impex.ColumnDefinition;

@Service
public class PrimitiveValueResolver implements ImpexValueResolver {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public <T> T resolve(String value, Class<T> type, List<Class<?>> genericArguments,
			ColumnDefinition columnDefinition) throws ValueResolverException {
		try {
			if (type.isAssignableFrom(value.getClass())) {
				return (T) value;
			} else {
				return mapper.readValue(value, type);
			}
		} catch (IOException e) {
			throw new ValueResolverException(e);
		}
	}

	private Boolean toBoolean(String value) {
		return BooleanUtils.toBooleanObject(value);
	}

	private Number toNumber(String value) {
		if (NumberUtils.isCreatable(value)) {
			return NumberUtils.createNumber(value);
		}

		return null;
	}
}
