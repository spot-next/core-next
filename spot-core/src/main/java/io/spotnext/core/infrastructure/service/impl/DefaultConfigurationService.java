
package io.spotnext.core.infrastructure.service.impl;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.LoggingService;

@Service
public class DefaultConfigurationService extends BeanAware implements ConfigurationService, EnvironmentAware {

	@Resource
	protected LoggingService loggingService;

	@Resource
	protected StandardEnvironment environment;

	@Override
	public String getString(final String key) {
		return getProperty(key);
	}

	@Override
	public Integer getInteger(final String key) {
		Integer value = null;

		try {
			final String v = getProperty(key);

			if (v != null) {
				value = Integer.parseInt(v);
			}
		} catch (final NumberFormatException e) {
			loggingService.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	@Override
	public Double getDouble(final String key) {
		Double value = null;

		try {
			value = Double.parseDouble(getProperty(key));
		} catch (final NumberFormatException e) {
			loggingService.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	@Override
	public String getString(final String key, final String defaultValue) {
		final String val = getString(key);

		return val != null ? val : defaultValue;
	}

	@Override
	public Integer getInteger(final String key, final Integer defaultValue) {
		Integer val = getInteger(key);

		if (val == null) {
			val = defaultValue;
		}

		return val != null ? val : defaultValue;
	}

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
	public Properties getPropertiesForPrefix(String prefix) {
		final Properties ret = new Properties();
		
		for (final PropertySource<?> s : environment.getPropertySources()) {
			if (s instanceof EnumerablePropertySource) {
				for (final String k : ((EnumerablePropertySource<?>) s).getPropertyNames()) {
					if (k.startsWith(prefix) && k.length() > prefix.length()) {
						ret.put(k.substring(prefix.length()), environment.getProperty(k));
					}
				}
			} else {
				loggingService.warn(String.format("Ignoring property source of type %s", s.getClass().getName()));
			}
		}

		return ret;
	}

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
			loggingService.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	@Override
	public boolean getBoolean(final String key, final boolean defaultValue) {
		Boolean b = getBoolean(key);

		if (b == null) {
			b = defaultValue;
		}

		return b;
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = (StandardEnvironment) environment;
	}
}
