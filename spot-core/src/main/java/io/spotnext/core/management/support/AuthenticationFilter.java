package io.spotnext.core.management.support;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * <p>AuthenticationFilter interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface AuthenticationFilter extends Filter {
	/** {@inheritDoc} */
	@Override
	void handle(Request request, Response response) throws AuthenticationException;
}
