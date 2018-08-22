package io.spotnext.core.management.support;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import spark.Request;
import spark.Response;

/**
 * This filter accepts all incoming requests.
 */
@Service
public class NoAuthenticationFilter implements AuthenticationFilter {
	@Override
	public void handle(final Request request, final Response response) throws AuthenticationException {
		// do nothing
	}
}