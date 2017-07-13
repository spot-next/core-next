package at.spot.spring.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import at.spot.spring.web.constants.SpringWebSupportConstants;

/**
 * This filter sets a cookie with the current CSRF token. This can be useful for
 * pure REST-based applications.
 * 
 * Hint: set this filter after the {@link CsrfFilter} in the security filter
 * chain.
 */
public class CsrfCookieResponseFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

		final CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

		if (csrf != null) {
			Cookie cookie = WebUtils.getCookie(request, SpringWebSupportConstants.CSRF_TOKEN_NAME);

			final String token = csrf.getToken();

			if (cookie == null || token != null && !token.equals(cookie.getValue())) {
				cookie = new Cookie(SpringWebSupportConstants.CSRF_TOKEN_NAME, token);
				cookie.setPath(request.getServletContext().getContextPath());
				response.addCookie(cookie);
			}
		}

		filterChain.doFilter(request, response);
	}
}