package io.spotnext.core.infrastructure.resolver.impex;

import java.util.List;

import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;

public interface ImpexValueResolver {
	/**
	 * Resolves the given string to an instance of the given type.
	 */
	<T> T resolve(String value, Class<T> targetType, List<Class<?>> genericArguments, ColumnDefinition columnDefinition)
			throws ValueResolverException;

}
