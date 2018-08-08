package io.spotnext.sample.tests;

import org.junit.Assert;
import org.junit.Test;

import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.sample.itemtype.sample.enumeration.UserType;

public class UserPrepareInterceptorIT extends AbstractIntegrationTest {

	@Override
	protected void prepareTest() {
	}

	@Override
	protected void teardownTest() {
	}

	/**
	 * If the database is initialized all sequences start with 0. Therefore the
	 * newly created user should be "user-0".
	 */
	@Test
	public void testUserPrepareInterceptor() {
		final User user = modelService.create(User.class);

		modelService.save(user);
		modelService.refresh(user);

		Assert.assertEquals(UserType.REGISTERED, user.getType());
	}
}
