package at.spot.core.infrastructure.service;

public interface ConfigurationService {

	/**
	 * Returns a String value for the given key or null, if the key doesn't
	 * exist.
	 * 
	 * @param key
	 * @return
	 */
	String getString(String key);

	/**
	 * Returns a String value for the given key or null, if the key doesn't
	 * exist.
	 * 
	 * @param key
	 * @return
	 */
	String getString(String key, String defaultValue);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't
	 * exist.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	Integer getInteger(String key);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't
	 * exist.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	Integer getInteger(String key, Integer defaultValue);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't
	 * exist.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	Double getDouble(String key);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't
	 * exist.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	Double getDouble(String key, Double defaultValue);

}
