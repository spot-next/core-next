package at.spot.core.infrastructure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.LoggingService;

@Service
public abstract class AbstractService extends BeanAware {

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected ConfigurationService configurationService;

	public LoggingService getLoggingService() {
		return loggingService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

}
