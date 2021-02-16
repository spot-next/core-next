package io.spotnext.core.management.support;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import spark.Request;
import spark.Response;

/**
 * This filter should not be used. It is just used to mark an endpoint method.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class NoopAuthenticationFilter implements AuthenticationFilter {
	/** {@inheritDoc} */
	@Override
	public void handle(final Request request, final Response response) throws AuthenticationException {
		throw new NotImplementedException(this.getClass().getName());
	}
}
