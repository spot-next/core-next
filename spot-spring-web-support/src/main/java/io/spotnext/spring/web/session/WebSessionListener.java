package io.spotnext.spring.web.session;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;

import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import io.spotnext.spring.web.constants.SpringWebSupportConstants;

/**
 * Implements a session listener that connects the application containers
 * session to the {@link io.spotnext.infrastructure.service.SessionService}. It also listens to authentications and
 * sets the authenticated used to the backend session.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@WebListener
public class WebSessionListener
		implements HttpSessionListener, ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	/** {@inheritDoc} */
	@Override
	public void sessionCreated(final HttpSessionEvent event) {
		//
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public void onApplicationEvent(final InteractiveAuthenticationSuccessEvent event) {
		final UserDetails userDetails = (UserDetails) event.getAuthentication().getDetails();

		getUserService().setCurrentUser(getUserService().getUser(userDetails.getUsername()));
	}

	protected UserService<User, UserGroup> getUserService() {
		return Registry.getApplicationContext().getBean(UserService.class);
	}

	protected SessionService getSessionService() {
		return Registry.getApplicationContext().getBean(SessionService.class);
	}
}
