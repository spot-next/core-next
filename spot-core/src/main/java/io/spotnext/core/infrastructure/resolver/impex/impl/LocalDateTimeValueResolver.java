package io.spotnext.core.infrastructure.resolver.impex.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.resolver.impex.ImpexValueResolver;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
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
public class LocalDateTimeValueResolver extends AbstractService implements ImpexValueResolver<LocalDateTime> {

	/** {@inheritDoc} */
	@Override
	public LocalDateTime resolve(final String value, final Class<LocalDateTime> targetType, final List<Class<?>> genericArguments,
			final ColumnDefinition columnDefinition) throws ValueResolverException {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		return LocalDateTime.parse(value);
	}
}
