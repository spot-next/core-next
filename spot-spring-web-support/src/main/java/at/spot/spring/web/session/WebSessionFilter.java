package at.spot.spring.web.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.support.Session;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.spring.web.constants.SpringWebSupportConstants;

/**
 * Sets the current session calling
 * {@link SessionService#setCurrentSession(Session)}.
 */
public class WebSessionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

		final String spotSessionId = (String) request.getSession()
				.getAttribute(SpringWebSupportConstants.SPOT_SESSION_ID);

		if (StringUtils.isNotBlank(spotSessionId)) {
			final Session session = getSessionService().getSession(spotSessionId);

			if (session != null) {
				getSessionService().setCurrentSession(session);
			} else {
				getLoggingService().warn(String.format("No session found for session id %s", spotSessionId));

				request.getSession().invalidate();

				// session = getSessionService().createSession(true);
				//
				// httpRequest.getSession().setAttribute(SpringWebSupportConstants.SPOT_SESSION_ID,
				// session.getId());
			}
		}

		filterChain.doFilter(request, response);
	}

	protected SessionService getSessionService() {
		return Registry.getApplicationContext().getBean(SessionService.class);
	}

	protected LoggingService getLoggingService() {
		return Registry.getApplicationContext().getBean(LoggingService.class);
	}

}
