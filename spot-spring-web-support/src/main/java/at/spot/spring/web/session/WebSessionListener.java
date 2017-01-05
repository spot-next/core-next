package at.spot.spring.web.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.support.Session;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;

/**
 * Implements a session listener that connects the application containers
 * session to the {@link SessionService}. It also listenes to authenticaitons
 * and sets the authenticated used to the backend session.
 */
@Service
public class WebSessionListener
		implements HttpSessionListener, ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	@Autowired
	protected SessionService sessionService;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Override
	public void sessionCreated(final HttpSessionEvent event) {
		// check if the web session has already a reference to the
		// backend session
		String spotSessionId = (String) event.getSession().getAttribute("spotSessionId");

		Session spotSession = null;

		// if yes then we fetch the backend session
		if (StringUtils.isNotBlank(spotSessionId)) {
			spotSession = sessionService.getSession(spotSessionId);
		}

		// if it is null we create a new one
		if (spotSession == null) {
			spotSession = sessionService.createSession(true);
			spotSessionId = spotSession.getId();

			// and store the session id in the web session
			event.getSession().setAttribute("spotSessionId", spotSessionId);
		}
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent event) {
		sessionService.closeSession(event.getSession().getId());
	}

	@Override
	public void onApplicationEvent(final InteractiveAuthenticationSuccessEvent event) {
		final UserDetails userDetails = (UserDetails) event.getAuthentication().getDetails();

		sessionService.getCurrentSession().user(userService.getUser(userDetails.getUsername()));
	}

}
