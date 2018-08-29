package io.spotnext.core.infrastructure.support;

import java.util.List;

/**
 * <p>MappingRegistry interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface MappingRegistry<KEY, VALUE> {
	/**
	 * <p>registerMapping.</p>
	 *
	 * @param key a KEY object.
	 * @param value a VALUE object.
	 */
	void registerMapping(KEY key, VALUE value);

	/**
	 * <p>unregisterMapping.</p>
	 *
	 * @param key a KEY object.
	 * @param value a VALUE object.
	 */
	void unregisterMapping(KEY key, VALUE value);

	/**
	 * Returns a list of all registered values for the given key
	 *
	 * @return is never null.
	 * @param key a KEY object.
	 */
	List<VALUE> getValues(KEY key);
}
