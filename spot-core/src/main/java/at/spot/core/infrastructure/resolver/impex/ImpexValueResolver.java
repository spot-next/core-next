package at.spot.core.infrastructure.resolver.impex;

import at.spot.core.infrastructure.exception.ValueResolverException;
import at.spot.core.infrastructure.support.impex.ColumnDefinition;

public interface ImpexValueResolver {
	/**
	 * Resolves the given string to an instance of the given type.
	 */
	<T> T resolve(String value, Class<T> targetType, ColumnDefinition columnDefinition) throws ValueResolverException;
}
