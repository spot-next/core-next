package io.spotnext.core.infrastructure.support.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class BooleanConverter implements Converter<Boolean, Boolean> {

	@Override
	public Boolean convert(final Boolean value) {
		return value != null ? value : false;
	}

}
