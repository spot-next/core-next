package io.spotnext.core.management.support;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import spark.Request;
import spark.Response;

/**
 * This filter handles HTTP Basic authentication. If the password is prefixed
 * with a 3-char prefix followed by a ":" (eg. "MD5:")
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class BasicAuthenticationFilter implements AuthenticationFilter {
	@Resource
	private AuthenticationService authenticationService;

	@Resource
	private UserService<User, UserGroup> userService;

	/** {@inheritDoc} */
	@Override
	public void handle(final Request request, final Response response) throws AuthenticationException {
		final User authenticatedUser = authenticate(request, response);

		if (authenticatedUser != null) {
			userService.setCurrentUser(authenticatedUser);
		} else {
			response.header("WWW-Authenticate", HttpAuthorizationType.BASIC.toString());
			throw new AuthenticationException("Could not authenticate user!");
		}
	}

	/**
	 * Uses the {@link AuthenticationService} to authenticate a user using a
	 * basic authentication request header fields.
	 * 
	 * @param request
	 * @param response
	 */
	protected User authenticate(final Request request, final Response response) {
		final String encodedHeader = StringUtils.trim(StringUtils.substringAfter(request.headers("Authorization"), HttpAuthorizationType.BASIC.toString()));

		User authenticatedUser = null;

		if (StringUtils.isNotBlank(encodedHeader)) {
			final String decodedHeader = new String(Base64.getDecoder().decode(encodedHeader), StandardCharsets.UTF_8);

			final String[] credentials = StringUtils.split(decodedHeader, ":", 2);

			if (credentials != null && credentials.length == 2) {
				boolean isEncrypted = false;

				// TODO handle other hash/encryption variants
				if (StringUtils.startsWith(credentials[1], "MD5:")) {
					/*
					 * the http authentication password is encoded in MD5, by
					 * default we are also using the MD5 password strategy, so
					 * we simply set {@link AuthenticationService#isEncrypted}
					 * to true
					 */
					isEncrypted = true;
				}

				authenticatedUser = authenticationService.getAuthenticatedUser(credentials[0], credentials[1], isEncrypted);
			}
		}

		return authenticatedUser;
	}
}
