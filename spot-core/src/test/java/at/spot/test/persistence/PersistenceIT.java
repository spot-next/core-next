package at.spot.test.persistence;

import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.internationalization.Currency;
import at.spot.itemtype.core.internationalization.LocalizationValue;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserAddress;
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
	public void testLocalizedString() {
		Currency currency = modelService.create(Currency.class);
		currency.setIsoCode("EUR");

//		LocalizedString name = new LocalizedString();
//		name.set("Euro", Locale.ENGLISH);
//		name.set("Euro", Locale.GERMAN);
		currency.setName(Collections.singletonMap(Locale.ENGLISH, "EURO"));

		modelService.save(currency);

		Currency loadedCurrency = modelService.get(Currency.class, currency.getPk());

		Assert.assertEquals(currency.getName().get(Locale.ENGLISH), loadedCurrency.getName().get(Locale.ENGLISH));
	}

	@Test
	public void testBidirectionalOne2ManyRelationUpdateReferenceOnChildSideWithLoadedUser() throws Exception {
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester1"));

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("asf");
		user.getAddresses().add(address);

		modelService.save(user);

		final User loadedUser = modelService.get(User.class, user.getPk());

		Assert.assertEquals(loadedUser.getAddresses().iterator().next().getPk(), address.getPk());
	}

	// TODO: manytoone mapping not working yet
	@Test
	public void testBidirectionalOne2ManyRelationUpdateReferenceOnChildSide() throws Exception {
		final User user = modelService.create(User.class);
		user.setId("testUser");

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("asf");
		user.getAddresses().add(address);

		modelService.save(user);

		final User loadedUser = modelService.get(User.class, user.getPk());

		Assert.assertEquals(loadedUser.getAddresses().iterator().next().getPk(), address.getPk());
	}

	@Test
	public void testBidirectionalOne2ManyRelation() throws Exception {
		final User user = modelService.create(User.class);
		user.setId("testUser");

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("asf");
		address.setOwner(user);

		modelService.save(address);

		final User loadedUser = modelService.get(User.class, user.getPk());

		Assert.assertEquals(loadedUser.getAddresses().iterator().next().getPk(), address.getPk());
	}

	@Test
	public void testBidirectionalMany2ManyRelations() throws Exception {
		final UserGroup group = modelService.create(UserGroup.class);
		group.setId("testGroup");

		final User user = modelService.create(User.class);
		user.setId("testUser");
		user.getGroups().add(group);

		modelService.save(user);

		final User loadedUser = modelService.get(User.class, user.getPk());
		final UserGroup loadedGroup = modelService.get(UserGroup.class, group.getPk());

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
