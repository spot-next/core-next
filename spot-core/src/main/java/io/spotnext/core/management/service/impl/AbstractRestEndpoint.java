package io.spotnext.core.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.http.ExceptionResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
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

	@Autowired
	protected SerializationService serializationService;

	protected HttpResponse handleGenericException(final Exception e) {
		final Throwable cause = (e instanceof TransactionException && e.getCause() != null) ? e.getCause() : e;
	
		return ExceptionResponse.withStatus(HttpStatus.BAD_REQUEST).withError("error.general", cause.getClass().getSimpleName() + ": " + cause.getMessage());
	}

}
