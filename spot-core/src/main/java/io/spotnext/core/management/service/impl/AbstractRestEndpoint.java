package io.spotnext.core.management.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.service.TypeService;

/**
 * <p>AbstractRestEndpoint class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class AbstractRestEndpoint {

	@Autowired
	protected TypeService typeService;

	@Resource
	protected SerializationService serializationService;

}
