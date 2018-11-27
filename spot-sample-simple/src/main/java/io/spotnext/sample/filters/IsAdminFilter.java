package io.spotnext.sample.filters;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.itemtype.core.beans.UserData;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import spark.Request;
import spark.Response;

/**
 * Checks if the current user is in the "admin" usergroup.
 */
@Service
public class IsAdminFilter implements AuthenticationFilter {

	@Autowired
	private UserService<User, UserGroup> userService;

	@Override
	public void handle(final Request request, final Response response) throws AuthenticationException {
		final UserData currentUser = userService.getCurrentUser();

		if (currentUser != null && userService.isUserInGroup(currentUser.getUid(), "admin")) {
			request.attribute("isLoggedIn", false);
			response.redirect("/");
		}

		request.attribute("isLoggedIn", true);
	}
}