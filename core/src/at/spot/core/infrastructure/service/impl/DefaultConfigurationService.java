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

}
