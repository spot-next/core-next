package io.spotnext.core.infrastructure.service;

import java.util.Properties;

public interface ConfigurationService {

	/**
	 * Returns a String value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key the key of the property
	 */
	String getString(String key);

	/**
	 * Returns a String value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 */
	String getString(String key, String defaultValue);

	/**
	 * Returns all properties that start with given prefix.
	 * 
	 * @param prefix that is used to find properties.
	 * @return the properties matching the given prefix (empty if nothing is found)
	 */
	Properties getPropertiesForPrefix(String prefix);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key the key of the property
	 */
	Integer getInteger(String key);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 */
	Integer getInteger(String key, Integer defaultValue);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key the key of the property
	 */
	Double getDouble(String key);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 */
	Double getDouble(String key, Double defaultValue);

	/**
	 * Returns a Boolean value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key the key of the property
	 */
	Boolean getBoolean(String key);

	/**
	 * Returns a Boolean value for the given key or null, if the key doesn't exist.
	 * 
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 */
	boolean getBoolean(String key, boolean defaultValue);

	/**
	 * Registers a properties file configuration, possibly overwriting already
	 * defined properties.
	 * 
	 * @param propertiesFile the properties filename.
	 * @throws ConfigurationException
	 */
//	void addConfiguration(String propertiesFile) throws ConfigurationException;
}
