package io.spotnext.core.management.support;

import org.springframework.stereotype.Service;

import spark.Request;
import spark.Response;

/**
 * This filter accepts all incoming requests.
 */
@Service
public class NoAuthenticationFilter implements AuthenticationFilter {
	@Override
	public void handle(Request request, Response response) throws Exception {
		// do nothing
	}
}