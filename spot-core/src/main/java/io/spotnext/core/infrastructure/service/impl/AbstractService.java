package io.spotnext.core.infrastructure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.LoggingService;

/**
 * <p>Abstract AbstractService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public abstract class AbstractService extends BeanAware {

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected ConfigurationService configurationService;

	/**
	 * <p>Getter for the field <code>loggingService</code>.</p>
	 *
	 * @return a {@link io.spotnext.core.infrastructure.service.LoggingService} object.
	 */
	public LoggingService getLoggingService() {
		return loggingService;
	}

	/**
	 * <p>Getter for the field <code>configurationService</code>.</p>
	 *
	 * @return a {@link io.spotnext.core.infrastructure.service.ConfigurationService} object.
	 */
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

}
