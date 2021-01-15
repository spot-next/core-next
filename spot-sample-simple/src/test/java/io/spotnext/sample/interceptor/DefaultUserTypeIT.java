package io.spotnext.sample.interceptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.spotnext.core.CoreInit;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.IntegrationTest;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.sample.SampleInit;
import io.spotnext.sample.types.enumerations.UserType;

@IntegrationTest(initClass = SampleInit.class)
@SpringBootTest(classes = { SampleInit.class, CoreInit.class })
public class DefaultUserTypeIT extends AbstractIntegrationTest {

	@Override
	protected void prepareTest() {
	}

	@Override
	protected void teardownTest() {
	}

	/**
	 * Check if the default user type has been set. 
	 */
	@Test
	public void testUserPrepareInterceptor() {
		final User user = modelService.create(User.class);

		modelService.save(user);
		modelService.refresh(user);

		Assertions.assertEquals(UserType.REGISTERED, user.getType());
	}
}
