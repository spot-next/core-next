package io.spotnext.core.infrastructure.service;

/**
 * <p>ConversionService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ConversionService {

	/**
	 * Converts a given source object to a given target object. This is done by
	 * matching the property names of both objects.
	 *
	 * @param source a S object.
	 * @param target a T object.
	 * @param <S> a S object.
	 * @param <T> a T object.
	 */
	<S, T> void convert(S source, T target);
}
