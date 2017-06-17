package at.spot.spring.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import at.spot.core.infrastructure.exception.SerializationException;
import at.spot.core.infrastructure.service.SerializationService;
import at.spot.spring.web.dto.AuthenticationResponse;

public class RestAuthenticationHandler
		implements AuthenticationSuccessHandler, AuthenticationFailureHandler, LogoutSuccessHandler {

	@Resource
	protected SerializationService serializationService;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		final AuthenticationResponse ret = new AuthenticationResponse();
		ret.setAuthenticated(true);
		ret.setSessionId(request.getSession().getId());
		ret.setUsername(getUsername(authentication));

		setStatusCode(response, HttpServletResponse.SC_OK, ret);
	}

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException exception) throws IOException, ServletException {

		final AuthenticationResponse ret = new AuthenticationResponse();

		setStatusCode(response, HttpServletResponse.SC_UNAUTHORIZED, ret);
	}

	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {

		final AuthenticationResponse ret = new AuthenticationResponse();
		ret.setUsername(getUsername(authentication));
		ret.setRedirectUrl("/");

		setStatusCode(response, HttpServletResponse.SC_OK, ret);
	}

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

	protected void setStatusCode(final HttpServletResponse response, final int code,
			final AuthenticationResponse payload) throws IOException {

		response.setStatus(code);
		response.setContentType("application/json");

		try {
			response.getWriter().println(serializationService.toJson(payload));
		} catch (final SerializationException e) {
			throw new IOException("Could not serialize authentication response payload", e);
		}

		response.getWriter().flush();
	}
}
