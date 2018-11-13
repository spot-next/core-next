package io.spotnext.spring.web.facade;

import javax.annotation.Resource;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.itemtype.core.beans.UserData;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

/**
 * <p>Abstract AbstractBaseFacade class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractBaseFacade {
	@Resource
	protected LoggingService loggingService;

	@Resource
	protected UserService<User, UserGroup> userService;

	protected UserData getCurrentUser() {
		final UserData user = userService.getCurrentUser();

		if (user == null) {
			throw new IllegalStateException("No user stored in session.");
		}

		return user;
	}
}
