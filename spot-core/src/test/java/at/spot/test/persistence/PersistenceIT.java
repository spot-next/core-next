package at.spot.test.persistence;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.internationalization.LocalizationValue;
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
		final UserGroup group = modelService.create(UserGroup.class);
		group.setId("testGroup");

		final User user = modelService.create(User.class);
		user.setId("testUser");
		user.getGroups().add(group);

		modelService.save(user);

		final User loadedUser = modelService.get(User.class, user.getPk());
		final UserGroup loadedGroup = modelService.get(UserGroup.class, group.getPk());

		modelService.refresh(loadedGroup);
		modelService.refresh(loadedUser);

		Assert.assertEquals(loadedUser.getGroups().iterator().next().getPk(), loadedGroup.getPk());
		Assert.assertEquals(loadedUser.getPk(), loadedGroup.getMembers().iterator().next().getPk());
		Assert.assertEquals(user.getId(), loadedUser.getId());
		Assert.assertEquals(group.getPk(), loadedGroup.getPk());
	}

	@Test
	public void testQueryByExample() throws Exception {
		final LocalizationValue localization = modelService.create(LocalizationValue.class);
		localization.setId("test.key");
		localization.setLocale(Locale.ENGLISH);

		modelService.save(localization);

		final LocalizationValue example = new LocalizationValue();
		example.setId("test.key");
		example.setLocale(Locale.ENGLISH);

		final LocalizationValue loaded = modelService.getByExample(example);

		Assert.assertEquals(localization.getId(), loaded.getId());
	}

	@Test
	public void testSerialNumberGeneration() throws Exception {
		final User user = modelService.create(User.class);
		user.setShortName("test user");

		modelService.save(user);

		Assert.assertNotNull(user.getId());
	}

}
