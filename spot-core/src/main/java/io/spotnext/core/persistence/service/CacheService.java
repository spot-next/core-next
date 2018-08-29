package io.spotnext.core.persistence.service;

import java.util.Map;

import io.spotnext.core.persistence.cache.CacheRegion;

/**
 * Provides a very basic cache service.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface CacheService {
	/**
	 * Returns the available cache regions
	 *
	 * @return a {@link java.util.Map} object.
	 */
	<K, V> Map<String, CacheRegion<K, V>> getCacheRegins();

	/**
	 * Adds a new cache region
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param cacheEntryKeyType a {@link java.lang.Class} object.
	 * @param entryType a {@link java.lang.Class} object.
	 */
	<K, V> void addCacheRegins(String name, Class<K> cacheEntryKeyType, Class<V> entryType);

	/**
	 * R€turns a cached value for the given key, or null if nothing is cached.
	 *
	 * @param regionName a {@link java.lang.String} object.
	 * @param key a K object.
	 * @param <K> a K object.
	 * @param <V> a V object.
	 * @return a V object.
	 */
	<K, V> V getCacheEntry(String regionName, K key);

	/**
	 * Adds the value to the given region cache under the given key.
	 *
	 * @param regionName a {@link java.lang.String} object.
	 * @param key a K object.
	 * @param value a V object.
	 * @param <K> a K object.
	 * @param <V> a V object.
	 */
	<K, V> void addCacheValue(String regionName, K key, V value);

	/**
	 * Invalidates a cached object with the given key.
	 *
	 * @param regionName a {@link java.lang.String} object.
	 * @param key a K object.
	 * @param <K> a K object.
	 */
	<K> void invalidateByKey(String regionName, K key);

	/**
	 * Invalidates the given cached value if still cached. Typically slower than
	 * providing a key to the object.
	 *
	 * @param regionName a {@link java.lang.String} object.
	 * @param value a V object.
	 * @param <V> a V object.
	 */
	<V> void invalidate(String regionName, V value);
}
