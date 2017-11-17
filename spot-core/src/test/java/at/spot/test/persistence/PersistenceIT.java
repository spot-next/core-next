package at.spot.test.persistence;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.internationalization.Country;
import at.spot.itemtype.core.user.Address;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserAddress;
import at.spot.itemtype.core.user.UserGroup;

public class PersistenceIT extends AbstractIntegrationTest {

	Country countryAT;

	@Override
	protected void prepareTest() throws Exception {
		countryAT = modelService.create(Country.class);
		countryAT.setIso3Code("AUT");

		modelService.save(countryAT);
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

		// user.getGroups().add(group);
		modelService.save(user);

		group.getMembers().add(user);
		modelService.save(group);

		final Map<String, Object> query = new HashMap<>();
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

	@Test
	public void testOneToOneRelation() throws Exception {
		final Address address = modelService.create(UserAddress.class);
		address.setCountry(countryAT);
		address.setCity("Wien");

		modelService.save(address);

		final Address exampleAddress = new UserAddress();
		exampleAddress.setCity("Wien");

		final Address loadedAddress = modelService.get(Address.class, exampleAddress);

		Assert.assertEquals(loadedAddress.getCountry().getIso3Code(), countryAT.getIso3Code());
	}

}