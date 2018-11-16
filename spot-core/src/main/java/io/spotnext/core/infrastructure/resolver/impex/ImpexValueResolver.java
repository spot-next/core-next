package io.spotnext.core.infrastructure.resolver.impex;

import java.util.List;

import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;

/**
 * <p>ImpexValueResolver interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ImpexValueResolver<T> {
	/**
	 * Resolves the given string to an instance of the given type.
	 *
	 * @param value a {@link java.lang.String} object.
	 * @param targetType a {@link java.lang.Class} object.
	 * @param genericArguments a {@link java.util.List} object.
	 * @param columnDefinition a {@link io.spotnext.infrastructure.support.impex.ColumnDefinition} object.
	 * @return a T object.
	 * @throws io.spotnext.infrastructure.exception.ValueResolverException if any.
	 */
	T resolve(String value, Class<T> targetType, List<Class<?>> genericArguments, ColumnDefinition columnDefinition)
			throws ValueResolverException;

}
