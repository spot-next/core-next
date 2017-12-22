package at.spot.test.persistence;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.internationalization.LocalizationKey;
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

		final User loadedUser = modelService.get(User.class, user.getPk());
		final UserGroup loadedGroup = modelService.get(UserGroup.class, group.getPk());

		Assert.assertEquals(loadedUser.getGroups().get(0).getPk(), loadedGroup.getPk());
		Assert.assertEquals(loadedUser.getPk(), loadedGroup.getMembers().get(0).getPk());
		Assert.assertEquals(user.getId(), loadedUser.getId());
		Assert.assertEquals(group.getPk(), loadedGroup.getPk());
	}

	@Test
	public void testQueryByExample() throws Exception {
		final LocalizationKey localization = modelService.create(LocalizationKey.class);
		localization.setId("test.key");
		localization.setLocale(Locale.ENGLISH);

		modelService.save(localization);

		final LocalizationKey example = new LocalizationKey();
		example.setId("test.key");
		example.setLocale(Locale.ENGLISH);

		final LocalizationKey loaded = modelService.getByExample(example);

		Assert.assertEquals(localization.getId(), loaded.getId());
	}

}
