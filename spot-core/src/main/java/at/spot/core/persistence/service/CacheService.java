package at.spot.core.persistence.service;

import java.util.Map;

import at.spot.core.persistence.cache.CacheRegion;

/**
 * Provides a very basic cache service.
 */
public interface CacheService {
	/**
	 * Returns the available cache regions
	 */
	<K, V> Map<String, CacheRegion<K, V>> getCacheRegins();

	/**
	 * Adds a new cache region
	 */
	<K, V> void addCacheRegins(String name, Class<K> cacheEntryKeyType, Class<V> entryType);

	/**
	 * R€turns a cached value for the given key, or null if nothing is cached.
	 * 
	 * @param regionName
	 * @param key
	 */
	<K, V> V getCacheEntry(String regionName, K key);

	/**
	 * Adds the value to the given region cache under the given key.
	 * 
	 * @param regionName
	 * @param key
	 * @param value
	 */
	<K, V> void addCacheValue(String regionName, K key, V value);

	/**
	 * Invalidates a cached object with the given key.
	 * 
	 * @param regionName
	 * @param key
	 */
	<K> void invalidateByKey(String regionName, K key);

	/**
	 * Invalidates the given cached value if still cached. Typically slower than
	 * providing a key to the object.
	 * 
	 * @param regionName
	 * @param value
	 */
	<V> void invalidate(String regionName, V value);
}
