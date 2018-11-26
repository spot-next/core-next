package io.spotnext.core.infrastructure.resolver.impex.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.resolver.impex.ImpexValueResolver;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;

/**
 * <p>
 * ReferenceValueResolver class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class LocalTimeValueResolver extends AbstractDateTimeValueResolver implements ImpexValueResolver<LocalTime> {

	/** {@inheritDoc} */
	@Override
	public LocalTime resolve(final String value, final Class<LocalTime> targetType, final List<Class<?>> genericArguments,
			final ColumnDefinition columnDefinition) throws ValueResolverException {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		return LocalTime.parse(value, getFormatter(columnDefinition, DateTimeFormatter.ISO_TIME));
	}
}
