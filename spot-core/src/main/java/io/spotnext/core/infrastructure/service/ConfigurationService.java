package io.spotnext.core.infrastructure.service;

import java.util.Properties;

/**
 * <p>ConfigurationService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ConfigurationService {

	/**
	 * Returns a String value for the given key or null, if the key doesn't exist.
	 *
	 * @param key the key of the property
	 * @return a {@link java.lang.String} object.
	 */
	String getString(String key);

	/**
	 * Returns a String value for the given key or null, if the key doesn't exist.
	 *
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 * @return a {@link java.lang.String} object.
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
	 * @return a {@link java.lang.Integer} object.
	 */
	Integer getInteger(String key);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 *
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 * @return a {@link java.lang.Integer} object.
	 */
	Integer getInteger(String key, Integer defaultValue);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 *
	 * @param key the key of the property
	 * @return a {@link java.lang.Double} object.
	 */
	Double getDouble(String key);

	/**
	 * Returns a Integer value for the given key or null, if the key doesn't exist.
	 *
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 * @return a {@link java.lang.Double} object.
	 */
	Double getDouble(String key, Double defaultValue);

	/**
	 * Returns a Boolean value for the given key or null, if the key doesn't exist.
	 *
	 * @param key the key of the property
	 * @return a {@link java.lang.Boolean} object.
	 */
	Boolean getBoolean(String key);

	/**
	 * Returns a Boolean value for the given key or null, if the key doesn't exist.
	 *
	 * @param key          the key of the property
	 * @param defaultValue the default value, in case the key is not found.
	 * @return a boolean.
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
