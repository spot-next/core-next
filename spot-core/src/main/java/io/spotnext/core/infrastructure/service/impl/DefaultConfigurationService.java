
package io.spotnext.core.infrastructure.service.impl;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.support.Logger;

/**
 * <p>
 * DefaultConfigurationService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultConfigurationService extends BeanAware implements ConfigurationService, EnvironmentAware {

	@Autowired
	protected ConfigurableEnvironment environment;

	/** {@inheritDoc} */
	@Override
	public String getString(final String key) {
		return getProperty(key);
	}

	/** {@inheritDoc} */
	@Override
	public Integer getInteger(final String key) {
		Integer value = null;

		try {
			final String v = getProperty(key);

			if (v != null) {
				value = Integer.parseInt(v);
			}
		} catch (final NumberFormatException e) {
			Logger.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	/** {@inheritDoc} */
	@Override
	public Double getDouble(final String key) {
		Double value = null;

		try {
			value = Double.parseDouble(getProperty(key));
		} catch (final NumberFormatException e) {
			Logger.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	/** {@inheritDoc} */
	@Override
	public String getString(final String key, final String defaultValue) {
		final String val = getString(key);

		return val != null ? val : defaultValue;
	}

	/** {@inheritDoc} */
	@Override
	public Integer getInteger(final String key, final Integer defaultValue) {
		Integer val = getInteger(key);

		if (val == null) {
			val = defaultValue;
		}

		return val != null ? val : defaultValue;
	}

	/** {@inheritDoc} */
	@Override
	public Double getDouble(final String key, final Double defaultValue) {
		Double val = getDouble(key);

		if (val == null) {
			val = defaultValue;
		}

		return val != null ? val : defaultValue;
	}

	/**
	 * Iterates over all registered properties files.
	 * 
	 * @param key
	 */
	protected String getProperty(final String key) {
		final String value = environment.getProperty(key);

		return value;
	}

	@Override
	public Properties getProperties() {
		return getProperties(null);
	}

	/** {@inheritDoc} */
	@Override
	public Properties getProperties(final String prefix) {
		final Properties ret = new Properties();

		final String sanitizedPrefix = StringUtils.trimToEmpty(prefix);

		for (final PropertySource<?> s : environment.getPropertySources()) {
			if (s instanceof EnumerablePropertySource) {
				for (final String propertyKey : ((EnumerablePropertySource<?>) s).getPropertyNames()) {
					if (sanitizedPrefix.isBlank() || propertyKey.startsWith(sanitizedPrefix)) {
						final Object value = environment.getProperty(propertyKey);

						if (value != null) {
							ret.put(propertyKey, value);
						}
					}
				}
			} else {
				Logger.debug(() -> String.format("Ignoring property source of type %s", s.getClass().getName()));
			}
		}

		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public Boolean getBoolean(final String key) {
		Boolean value = null;

		try {
			final String v = getProperty(key);

			// null is treated as false
			if (v != null) {
				value = Boolean.parseBoolean(v);
			}
		} catch (final NumberFormatException e) {
			Logger.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	/** {@inheritDoc} */
	@Override
	public boolean getBoolean(final String key, final boolean defaultValue) {
		Boolean b = getBoolean(key);

		if (b == null) {
			b = defaultValue;
		}

		return b;
	}

	@Override
	public void setProperty(String key, Object value) {
		System.setProperty(key, value instanceof String ? (String) value : value.toString());
	}

	/** {@inheritDoc} */
	@Override
	public void setEnvironment(final Environment environment) {
		if (environment instanceof StandardEnvironment) {
			this.environment = (StandardEnvironment) environment;
		} else {
			throw new IllegalStateException(String.format("Cannot handle environment of type %s", environment.getClass().getName()));
		}
	}
}
