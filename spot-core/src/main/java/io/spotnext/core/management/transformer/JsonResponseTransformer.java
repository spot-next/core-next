package io.spotnext.core.management.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.http.ExceptionResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.itemtype.core.beans.SerializationConfiguration;
import io.spotnext.itemtype.core.enumeration.DataFormat;

/**
 * Converts the given object to JSON.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class JsonResponseTransformer implements ResponseTransformer {

	@Autowired
	protected SerializationService serializationService;

	private static final SerializationConfiguration CONFIG = new SerializationConfiguration();
	static {
		CONFIG.setFormat(DataFormat.JSON);
	}

	/** {@inheritDoc} */
	@Override
	public String handleResponse(final Object object) throws Exception {
		return getSeriaizationService().serialize(CONFIG, object);
	}

	protected SerializationService getSeriaizationService() {
		if (serializationService == null)
			serializationService = Registry.getApplicationContext().getBean("serializationService",
					SerializationService.class);

		return serializationService;
	}

	/** {@inheritDoc} */
	@Override
	public String handleException(final Object object, Exception exception) throws Exception {
		// let the render method handle rendering
		return handleResponse(ExceptionResponse.withStatus(HttpStatus.INTERNAL_SERVER_ERROR, exception));
	}
}
