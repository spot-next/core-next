package at.spot.spring.web.session;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;
import at.spot.spring.web.constants.SpringWebSupportConstants;

/**
 * Implements a session listener that connects the application containers
 * session to the {@link SessionService}. It also listens to authentications and
 * sets the authenticated used to the backend session.
 */
@WebListener
public class WebSessionListener
		implements HttpSessionListener, ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	@Override
	public void sessionCreated(final HttpSessionEvent event) {
		//
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent event) {
		String spotSessionId = getSpotSessionid(event.getSession());

		if (StringUtils.isNotBlank(spotSessionId)) {
			getSessionService().closeSession(spotSessionId);
		}
	}

	protected String getSpotSessionid(HttpSession session) {
		return (String) session.getAttribute(SpringWebSupportConstants.SPOT_SESSION_ID);
	}

	@Override
	public void onApplicationEvent(final InteractiveAuthenticationSuccessEvent event) {
		final UserDetails userDetails = (UserDetails) event.getAuthentication().getDetails();

		getUserService().setCurrentUser(getUserService().getUser(userDetails.getUsername()));
	}

	protected LoggingService getLoggingService() {
		return Registry.getApplicationContext().getBean(LoggingService.class);
	}

	protected UserService<User, UserGroup> getUserService() {
		return Registry.getApplicationContext().getBean(UserService.class);
	}

	protected SessionService getSessionService() {
		return Registry.getApplicationContext().getBean(SessionService.class);
	}
}
