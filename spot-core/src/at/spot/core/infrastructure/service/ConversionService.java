package at.spot.core.infrastructure.service;

public interface ConversionService {

	/**
	 * Converts a given source object to a given target object. This is done by
	 * matching the property names of both objects.
	 * 
	 * @param source
	 * @param target
	 */
	<S, T> void convert(S source, T target);
}
