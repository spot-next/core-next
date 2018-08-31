package io.spotnext.test.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;
import io.spotnext.itemtype.core.internationalization.Currency;
import io.spotnext.itemtype.core.internationalization.LocalizationValue;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserAddress;
import io.spotnext.itemtype.core.user.UserGroup;

public class PersistenceIT extends AbstractIntegrationTest {

	@Rule
	public ExpectedException expectedExeption = ExpectedException.none();
	
	@Override
	protected void prepareTest() {
	}

	@Override
	protected void teardownTest() {
	}

	/**
	 * The catalogVersion Default:Online already be available through the initial
	 * data. Therefore saving a second item will cause a uniqueness constraint
	 * violation.
	 */
	@Test(expected = ModelSaveException.class)
	public void testUniqueConstraintOfSubclass() {
		final Catalog catalog = modelService.get(Catalog.class,
				Collections.singletonMap(Catalog.PROPERTY_ID, "Default"));

		final CatalogVersion version = modelService.create(CatalogVersion.class);
		version.setCatalog(catalog);
		version.setId("Online");

		modelService.save(version);
	}

	@Test
	public void testLocalizedString() {
		final Currency currency = modelService.create(Currency.class);
		currency.setIsoCode("EUR");

		final String german = "german";
		final String english = "english";

		currency.setName(english, Locale.UK);
		currency.setName(german, Locale.GERMANY);

		modelService.save(currency);

		final Currency loadedCurrency = modelService.get(Currency.class, currency.getPk());

		// these locales are not the same as the locales with country codes!
		Assert.assertNull(loadedCurrency.getName(Locale.ENGLISH));
		Assert.assertNull(loadedCurrency.getName(Locale.GERMAN));
		Assert.assertEquals(english, loadedCurrency.getName(Locale.UK));
		Assert.assertEquals(german, loadedCurrency.getName(Locale.GERMANY));
	}

	@Test
	public void testBidirectionalOne2ManyRelationUpdateReferenceOnChildSideWithLoadedUser() throws Exception {
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester1"));

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("Test street");
		user.getAddresses().add(address);

		modelService.save(user);

		final User loadedUser = modelService.get(User.class, user.getPk());

		Assert.assertEquals(1, loadedUser.getAddresses().size());
		Assert.assertEquals(loadedUser.getAddresses().iterator().next().getPk(), address.getPk());
	}

	@Test
	public void testBidirectionalOne2ManyRelationUpdateReferenceOnChildSide() throws Exception {
		final User user = modelService.create(User.class);
		user.setId("testUser1");

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("Test");
		user.getAddresses().add(address);

		loggingService.debug("Addresses before save: "
				+ user.getAddresses().stream().map(a -> "PK = " + a.getPk() + ", streetname = " + a.getStreetName())
						.collect(Collectors.joining(",")));

		modelService.save(user);

		loggingService.debug("Addresses after save: "
				+ user.getAddresses().stream().map(a -> "PK = " + a.getPk() + ", streetname = " + a.getStreetName())
						.collect(Collectors.joining(",")));

		final User loadedUser = modelService.get(User.class, user.getPk());

		loggingService.debug("Addresses after load: "
				+ user.getAddresses().stream().map(a -> "PK = " + a.getPk() + ", streetname = " + a.getStreetName())
						.collect(Collectors.joining(",")));

		Assert.assertEquals(1, loadedUser.getAddresses().size());
		Assert.assertEquals(loadedUser.getAddresses().iterator().next().getPk(), address.getPk());
	}

	@Test
	public void testBidirectionalOne2ManyRelation() throws Exception {
		final User user = modelService.create(User.class);
		user.setId("testUser2");

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("Test2");
		address.setOwner(user);

		modelService.save(address);

		final User loadedUser = modelService.get(User.class, user.getPk());

		Assert.assertEquals(loadedUser.getAddresses().iterator().next().getPk(), address.getPk());
	}

	@Test
	public void testBidirectionalMany2ManyRelations() throws Exception {
		final UserGroup group1 = modelService.create(UserGroup.class);
		group1.setId("testGroup 1");

		final UserGroup group2 = modelService.create(UserGroup.class);
		group2.setId("testGroup 2");

		final User user1 = modelService.create(User.class);
		user1.setId("testUser 1");
		user1.getGroups().add(group1);
		user1.getGroups().add(group2);

		final User user2 = modelService.create(User.class);
		user2.setId("testUser 2");
		user2.getGroups().add(group2);

		modelService.saveAll(user1, user2);

		final User loadedUser1 = modelService.get(User.class, user1.getPk());
		final User loadedUser2 = modelService.get(User.class, user2.getPk());

		final UserGroup loadedGroup1 = modelService.get(UserGroup.class, group1.getPk());
		final UserGroup loadedGroup2 = modelService.get(UserGroup.class, group2.getPk());

		Assert.assertEquals(2, loadedUser1.getGroups().size());
		Assert.assertTrue(loadedUser1.getGroups().contains(loadedGroup1));
		Assert.assertTrue(loadedUser1.getGroups().contains(loadedGroup2));

		Assert.assertEquals(1, loadedUser2.getGroups().size());
		Assert.assertTrue(loadedUser2.getGroups().contains(loadedGroup2));
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

	@Test
	public void testDetachedItemIsNotSaved() {
		final User admin = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "admin"));
		admin.setShortName("detached");
		modelService.detach(admin);

		final User loadedAdmin = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "admin"));

		assertEquals(null, loadedAdmin.getShortName());
	}

	@Test
	public void testValidateModelRecursively() {
		expectedExeption.expect(ModelSaveException.class);
		
		// TODO: how to handle localized messages?
//		expectedExeption.expectMessage("User.id must not be null");

		UserGroup group = modelService.create(UserGroup.class);
		group.setId("test");

		User user = modelService.create(User.class);
		group.getMembers().add(user);

		modelService.save(group);
	}
}
