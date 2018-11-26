package io.spotnext.core.infrastructure.resolver.impex.impl;

import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;

public abstract class AbstractDateTimeValueResolver extends AbstractService {

	protected DateTimeFormatter getFormatter(ColumnDefinition columnDefinition, DateTimeFormatter defaultFormatter) {
		final String format = columnDefinition.getModifiers().get("format");
		DateTimeFormatter formatter = null;

		if (StringUtils.isNotBlank(format)) {
			formatter = DateTimeFormatter.ofPattern(format);
		} else {
			formatter = defaultFormatter;
		}

		return formatter;
	}
}
