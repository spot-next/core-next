package at.spot.core.infrastructure.support;

import java.util.List;

public interface MappingRegistry<KEY, VALUE> {
	void registerMapping(KEY key, VALUE value);

	void unregisterMapping(KEY key, VALUE value);

	/**
	 * Returns a list of all registered values for the given key
	 * 
	 * @return is never null.
	 */
	List<VALUE> getValues(KEY key);
}
