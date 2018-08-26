package io.spotnext.core.management.transformer;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.support.spring.Registry;

/**
 * Converts the given object to JSON.
 */
@Service
public class JsonResponseTransformer implements ResponseTransformer {

	@Autowired
	protected SerializationService serializationService;

	@Override
	public String handleResponse(final Object object) throws Exception {
		return getSeriaizationService().toJson(object);
	}

	protected SerializationService getSeriaizationService() {
		if (serializationService == null)
			serializationService = Registry.getApplicationContext().getBean("serializationService",
					SerializationService.class);

		return serializationService;
	}

	@Override
	public String handleException(final Object object, Exception exception) throws Exception {
		final String message;

		if (exception instanceof InvocationTargetException) {
			InvocationTargetException ie = (InvocationTargetException) exception;
			message = ie.getTargetException() != null ? ie.getTargetException().getMessage() : exception.getMessage();
		} else {
			message = exception.getMessage();
		}

		// let the render method handle rendering
		return handleResponse(DataResponse.internalServerError().withError("error.internal", message));
	}
}
