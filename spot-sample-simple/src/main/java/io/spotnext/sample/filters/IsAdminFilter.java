package io.spotnext.sample.filters;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.AuthenticationException;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import spark.Request;
import spark.Response;

@Service
public class IsAdminFilter implements AuthenticationFilter {

	@Resource
	private UserService<User, UserGroup> userService;

	
	@Override
	public void handle(Request request, Response response) throws AuthenticationException {
		final User currentUser = userService.getCurrentUser();
		
		if (currentUser == null || !"admin".equals(currentUser.getId())) {
//			throw new AuthenticationException("Could not authenticate user!");
			response.redirect("/");
		}
	}

}
