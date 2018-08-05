package io.spotnext.spring.web.facade;

import javax.annotation.Resource;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public abstract class AbstractBaseFacade {
	@Resource
	protected LoggingService loggingService;

	@Resource
	protected UserService<User, UserGroup> userService;

	protected User getCurrentUser() {
		final User user = userService.getCurrentUser();

		if (user == null) {
			throw new IllegalStateException("No user stored in session.");
		}

		return user;
	}
}