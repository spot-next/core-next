package at.spot.spring.web.session;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import at.spot.core.infrastructure.http.Session;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.spring.web.constants.SpringWebSupportConstants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Sets the current session calling
 * {@link SessionService#setCurrentSession(Session)}.
 */
@SuppressFBWarnings("MS_PKGPROTECT")
public class WebSessionFilter extends OncePerRequestFilter {

	public final static String[] RESOURCE_SUFFIXES = { ".map", ".css", ".js", ".jpg", ".jpeg", ".gif", ".png", ".txt" };

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

		if (!isResourceRequest(request)) {

			// check if the web session has already a reference to the
			// backend session
			String spotSessionId = getSpotSessionid(request.getSession());

			Session spotSession = null;

			// if yes then we fetch the backend session
			if (StringUtils.isNotBlank(spotSessionId)) {
				spotSession = getSessionService().getSession(spotSessionId);
			}

			// if it is null we create a new one
			if (spotSession == null) {
				spotSession = getSessionService().createSession(true);
				getLoggingService().debug(String.format("Created new session %s", spotSession.getId()));

				// and store the session id in the web session
				request.getSession().setAttribute(SpringWebSupportConstants.SPOT_SESSION_ID, spotSession.getId());
			} else {
				// set the session for the current thread
				getSessionService().setCurrentSession(spotSession);
			}
		}

		filterChain.doFilter(request, response);
	}

	protected boolean isResourceRequest(HttpServletRequest request) {
		boolean ret = false;

		// firefox requests css source maps and doesn't send the cookie and no mime type
		// this results in a new http session (and therefore a new spot session)
		if (StringUtils.equals(request.getHeader("Accept"), "*/*")) {
			ret = true;
		}

		if (!ret) {
			ret = Stream.of(RESOURCE_SUFFIXES).anyMatch(s -> request.getRequestURL().toString().endsWith(s));
		}

		return ret;
	}

	protected String getSpotSessionid(HttpSession session) {
		return (String) session.getAttribute(SpringWebSupportConstants.SPOT_SESSION_ID);
	}

	protected SessionService getSessionService() {
		return Registry.getApplicationContext().getBean(SessionService.class);
	}

	protected LoggingService getLoggingService() {
		return Registry.getApplicationContext().getBean(LoggingService.class);
	}

}
