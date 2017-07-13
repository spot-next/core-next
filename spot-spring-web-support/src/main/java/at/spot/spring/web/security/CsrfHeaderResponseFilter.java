package at.spot.spring.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import at.spot.spring.web.constants.SpringWebSupportConstants;

/**
 * This filter sets a header field with the current CSRF token to the response.
 * This can be useful for pure REST-based applications.
 * 
 * Hint: set this filter after the {@link CsrfFilter} in the security filter
 * chain.
 */
public class CsrfHeaderResponseFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

		final CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

		if (csrf != null && csrf.getToken() != null) {
			response.setHeader(SpringWebSupportConstants.CSRF_TOKEN_NAME, csrf.getToken());
		}

		filterChain.doFilter(request, response);
	}
}