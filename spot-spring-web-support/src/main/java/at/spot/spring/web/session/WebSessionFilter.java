package at.spot.spring.web.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.GenericFilterBean;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.support.Session;
import at.spot.core.infrastructure.support.spring.Registry;

/**
 * Sets the current session calling
 * {@link SessionService#setCurrentSession(Session)}.
 */
public class WebSessionFilter extends GenericFilterBean {

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			// final HttpServletRequest httpRequest = (HttpServletRequest)
			// request;
			// final String spotSessionId = (String) httpRequest.getSession()
			// .getAttribute(SpringWebSupportConstants.SPOT_SESSION_ID);
			//
			// if (StringUtils.isNotBlank(spotSessionId)) {
			// Session session = getSessionService().getSession(spotSessionId);
			//
			// if (session != null) {
			// getSessionService().setCurrentSession(session);
			// } else {
			// getLoggingService().warn(String.format("No session found for
			// session id %s", spotSessionId));
			//
			// session = getSessionService().createSession(true);
			//
			// httpRequest.getSession().setAttribute(SpringWebSupportConstants.SPOT_SESSION_ID,
			// session.getId());
			// }
			// }
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
