package io.spotnext.core.infrastructure.service.impl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ConfigurationService;
import io.spotnext.core.infrastructure.service.ModelService;

/**
 * <p>
 * The base class for all services - provides useful services.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public abstract class AbstractService extends BeanAware implements DisposableBean {

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected ModelService modelService;

	/**
	 * @return the {@link ConfigurationService}
	 */
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	/**
	 * @return the {@link ModelService}
	 */
	public ModelService getModelService() {
		return modelService;
	}

	@Override
	public void destroy() throws Exception {
	}
}
