package io.spotnext.core.management.support;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import spark.Request;
import spark.Response;

/**
 * This filter accepts all incoming requests.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class NoAuthenticationFilter implements AuthenticationFilter {
	/** {@inheritDoc} */
	@Override
	public void handle(final Request request, final Response response) throws AuthenticationException {
		// do nothing
	}
}
