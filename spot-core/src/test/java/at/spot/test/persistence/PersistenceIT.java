package at.spot.test.persistence;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

public class PersistenceIT extends AbstractIntegrationTest {

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
		final User user = modelService.create(User.class);
		user.setId("testUser");

		final UserGroup group = modelService.create(UserGroup.class);
		group.setId("testGroup");

		modelService.save(user);

		user.getGroups().add(group);
		// group.getMembers().add(user);

		modelService.save(group);

		final Map<String, Comparable<?>> query = new HashMap<>();
		query.put("id", user.getId());

		final User loadedUser = modelService.get(User.class, query);
		final UserGroup loadedGroup = modelService.get(UserGroup.class, group.getPk());

		Assert.assertEquals(loadedUser.getGroups().get(0).getPk(), loadedGroup.getPk());
		Assert.assertEquals(loadedUser.getPk(), loadedGroup.getMembers().get(0).getPk());
		Assert.assertEquals(user.getId(), loadedUser.getId());
		Assert.assertEquals(group.getPk(), loadedGroup.getPk());
	}

	@Test
	public void testQueryByExample() throws Exception {
		final User user = modelService.create(User.class);
		user.setId("testUser");

		modelService.save(user);

		final User exampleUser = new User();
		exampleUser.setId("testUser");

		final User loadedUser = modelService.get(User.class, exampleUser);

		Assert.assertEquals(user.getId(), loadedUser.getId());
	}

}