package at.spot.spring.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import at.spot.core.infrastructure.exception.SerializationException;
import at.spot.core.infrastructure.service.SerializationService;
import at.spot.spring.web.dto.Response;
import at.spot.spring.web.dto.Status;
import at.spot.spring.web.dto.UserStatus;

/**
 * This handler implementation combines a few spring security handlers together.
 * It is primarily useful for RESTful services.
 */
public class RestAuthenticationHandler implements AuthenticationEntryPoint, AuthenticationSuccessHandler,
		AuthenticationFailureHandler, LogoutSuccessHandler, AccessDeniedHandler {

	@Resource
	protected SerializationService serializationService;

	/**
	 * Aborts a request in case it is not authenticated. It don't sends the
	 * www-authenticate header though as this would show a login box in the
	 * browser..
	 */
	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException exception) throws IOException, ServletException {

		onAuthenticationFailure(request, response, exception);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// authentication responses
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Called when authentication was success.
	 */
	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		final UserStatus ret = new UserStatus();
		ret.setAuthenticated(true);
		ret.setSessionId(request.getSession().getId());
		ret.setUsername(getUsername(authentication));

		sendResponse(response, HttpStatus.OK, ret, null);
	}

	/**
	 * Called when authentication failed.
	 */
	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException exception) throws IOException, ServletException {

		final UserStatus ret = new UserStatus();

		sendResponse(response, HttpStatus.UNAUTHORIZED, ret, exception);
	}

	/**
	 * Called when access to resource is denied.
	 */
	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response,
			final AccessDeniedException exception) throws IOException, ServletException {

		onAuthenticationFailure(request, response,
				new InsufficientAuthenticationException("Not authenticated", exception));
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// logout
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Called when logout was successful.
	 */
	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		final UserStatus ret = new UserStatus();
		ret.setUsername(getUsername(authentication));
		ret.setRedirectUrl("/");

		sendResponse(response, HttpStatus.OK, ret, null);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////

	protected String getUsername(final Authentication authentication) {
		String username = "";

		if (authentication.getDetails() instanceof User) {
			username = ((User) authentication.getDetails()).getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			username = (String) authentication.getPrincipal();
		}

		if (StringUtils.isBlank(username)) {
			username = authentication.getName();
		}

		return username;
	}

	protected void sendResponse(final HttpServletResponse response, final HttpStatus httpStatusCode,
			final UserStatus payload, final AuthenticationException exception) throws IOException {

		response.setStatus(httpStatusCode.value());
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

		final Response<UserStatus> ret = new Response<>();
		ret.setPayload(payload);

		if (exception != null) {
			ret.getErrors().add(new Status("login.error", exception.getMessage()));
		}

		try {
			response.getWriter().println(serializationService.toJson(ret));
		} catch (final SerializationException e) {
			throw new IOException("Could not serialize authentication response payload", e);
		}

		response.getWriter().flush();
	}

}
