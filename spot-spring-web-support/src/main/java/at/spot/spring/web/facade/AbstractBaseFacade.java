package at.spot.spring.web.facade;

import javax.annotation.Resource;

import at.spot.core.infrastructure.service.UserService;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;

public abstract class AbstractBaseFacade {
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
