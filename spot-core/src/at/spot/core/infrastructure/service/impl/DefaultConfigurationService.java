
package at.spot.core.infrastructure.service.impl;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ConfigurationService;

@Service
public class DefaultConfigurationService extends AbstractService implements ConfigurationService {

	@Autowired
	protected List<Properties> configuration;

	@Override
	public String getString(String key) {
		return getProperty(key);
	}

	@Override
	public Integer getInteger(String key) {
		Integer value = null;

		try {
			value = Integer.parseInt(getProperty(key));
		} catch (NumberFormatException e) {
			loggingService.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	@Override
	public Double getDouble(String key) {
		Double value = null;

		try {
			value = Double.parseDouble(getProperty(key));
		} catch (NumberFormatException e) {
			loggingService.exception(String.format("Can't load config key %s", key), e);
		}

		return value;
	}

	@Override
	public String getString(String key, String defaultValue) {
		String val = getString(key);

		return val != null ? val : defaultValue;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		Integer val = getInteger(key);

		if (val == null) {
			val = defaultValue;
		}

		return val != null ? val : defaultValue;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
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
	 * @return
	 */
	protected String getProperty(String key) {
		String value = null;

		for (Properties prop : configuration) {
			value = prop.getProperty(key);

			if (StringUtils.isNotBlank(value)) {
				break;
			}
		}

		return value;
	}
}
