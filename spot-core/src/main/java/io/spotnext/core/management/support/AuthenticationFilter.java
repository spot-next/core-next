package io.spotnext.core.management.support;

import spark.Filter;
import spark.Request;
import spark.Response;

public interface AuthenticationFilter extends Filter {
	/**
	 * @param request  The request object providing information about the HTTP
	 *                 request
	 * @param response The response object providing functionality for modifying the
	 *                 response
	 * @throws java.lang.Exception when handle fails
	 */
	void handle(Request request, Response response) throws Exception;
}
