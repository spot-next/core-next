
package at.spot.core.infrastructure.service.impl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ConfigurationService;

@Service
public class DefaultConfigurationService implements ConfigurationService {

	@Autowired
	protected Properties configuration;

	@Override
	public String getString(String key) {
		return configuration.getProperty(key);
	}

	@Override
	public Integer getInteger(String key) throws NumberFormatException {
		return Integer.parseInt(getString(key));
	}

	@Override
	public Double getDouble(String key) throws NumberFormatException {
		return Double.parseDouble(getString(key));
	}

	@Override
	public String getString(String key, String defaultValue) {
		String val = getString(key);

		return val != null ? val : defaultValue;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) throws NumberFormatException {
		Integer val = getInteger(key);

		return val != null ? val : defaultValue;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) throws NumberFormatException {
		Double val = getDouble(key);

		return val != null ? val : defaultValue;
	}

}
