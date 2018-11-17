package io.spotnext.core.infrastructure.support.spring;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

//@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
public class BooleanConverter implements Converter<Boolean, Boolean>, Formatter<Boolean> {

	@Override
	public Boolean convert(final Boolean value) {
		return value != null ? value : false;
	}

	@Override
	public String print(Boolean object, Locale locale) {
		return convert(object).toString();
	}

	@Override
	public Boolean parse(String text, Locale locale) throws ParseException {
		return BooleanUtils.toBoolean(text);
	}

}
