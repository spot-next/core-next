package io.spotnext.core.management.support;

import java.util.Properties;

import org.springframework.stereotype.Service;

import io.spotnext.itemtype.core.user.User;
import spark.Request;
import spark.Response;

/**
 * This filter accepts all incoming requests.
 */
@Service
public class BasicAuthenticationFilter implements AuthenticationFilter {
	private static final String BASIC_AUTHENTICATION_TYPE = "Basic";
	private static final int NUMBER_OF_AUTHENTICATION_FIELDS = 2;
	private static final String ACCEPT_ALL_TYPES = "*";

	@Override
	public void handle(final Request request, final Response response) {
		// only do authentication in case there are any authentication expressions are
		// defined

		Properties authenticationExpression = getAuthenticationExpression(request);

		// authenticate
		final User authenticatedUser = authenticate(request, response);

		if (authenticatedUser != null) {
			userService.setCurrentUser(authenticatedUser);
		} else {
			response.header("WWW-Authenticate", HttpAuthorizationType.BASIC.toString());
			service.halt(401);
		}
	}

}