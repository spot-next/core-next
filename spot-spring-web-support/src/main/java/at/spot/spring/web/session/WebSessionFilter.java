package at.spot.spring.web.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.support.Session;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.spring.web.constants.SpringWebSupportConstants;

/**
 * Sets the current session calling
 * {@link SessionService#setCurrentSession(Session)}.
 */
public class WebSessionFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String spotSessionId = (String) httpRequest.getSession()
					.getAttribute(SpringWebSupportConstants.SPOT_SESSION_ID);

			if (StringUtils.isNotBlank(spotSessionId)) {
				Session session = getSessionService().getSession(spotSessionId);

				if (session != null) {
					getSessionService().setCurrentSession(session);
				} else {
					getLoggingService().warn(String.format("No session found for session id %s", spotSessionId));
				}
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
