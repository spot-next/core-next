package at.spot.test.persistence;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

public class PersistenceIntegrationTest extends AbstractIntegrationTest {

	@Override
	protected void prepareTest() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void teardownTest() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testBidirectionalRelations() throws Exception {
		User user = modelService.create(User.class);
		user.setId("testUser");

		UserGroup group = modelService.create(UserGroup.class);
		group.setId("testGroup");

		modelService.save(user);

		user.setGroups(Arrays.asList(group));
		// group.getMembers().add(user);

		modelService.save(group);

		User loadedUser = modelService.get(User.class, user.getPk());
		UserGroup loadedGroup = modelService.get(UserGroup.class, group.getPk());

		Assert.assertEquals(user.getGroups().get(0).getPk(), loadedGroup.getPk());
		Assert.assertEquals(user.getPk(), loadedGroup.getMembers().get(0).getPk());
		Assert.assertEquals(user.getId(), loadedUser.getId());
		Assert.assertEquals(group.getPk(), loadedUser.getGroups().get(0).getPk());
	}

	@Test
	public void testQueryByExample() throws Exception {
		User user = modelService.create(User.class);
		user.setId("testUser1");

		modelService.save(user);

		User exampleUser = new User();
		exampleUser.setId("testUser1");

		User loadedUser = modelService.get(User.class, exampleUser);

		Assert.assertEquals(user.getId(), loadedUser.getId());
	}

}