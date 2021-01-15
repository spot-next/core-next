package io.spotnext.test.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.OneToMany;
import javax.persistence.OptimisticLockException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.persistence.service.SequenceGenerator;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.TestMocker;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;
import io.spotnext.itemtype.core.internationalization.Currency;
import io.spotnext.itemtype.core.internationalization.LocalizationValue;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserAddress;
import io.spotnext.itemtype.core.user.UserGroup;
import io.spotnext.support.util.ClassUtil;

public class PersistenceIT extends AbstractIntegrationTest {

	@Autowired
	protected TestMocker testMocker;

	@Autowired
	protected SequenceGenerator sequenceGenerator;

//	@Rule
//	public ExpectedException expectedExeption = ExpectedException.none();

	@Override
	protected void prepareTest() {
	}

	@Override
	protected void teardownTest() {
	}

	@Disabled
	@Test
	public void testDuplicateEntityAttachedToPersistenceContext() {
		final User loaded = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "tester1"));
		loaded.setShortName("loaded");

		final User duplicate = new User();
		ClassUtil.setField(duplicate, "id", loaded.getId());
		modelService.refresh(duplicate);
		duplicate.setShortName("loaded");

		modelService.save(duplicate);

		try {
			modelService.save(loaded);
		} catch (final ModelSaveException e) {
			assertTrue(e.getCause() instanceof OptimisticLockException);
		}
	}

	/**
	 * The catalogVersion Default:Online already be available through the initial data. Therefore saving a second item will cause a uniqueness constraint
	 * violation.
	 */
	@Test
	public void testUniqueConstraintOfSubclass() {
		final Catalog catalog = modelService.get(Catalog.class, Collections.singletonMap(Catalog.PROPERTY_UID, "Default"));

		final CatalogVersion version = modelService.create(CatalogVersion.class);
		version.setCatalog(catalog);
		version.setUid("Online");

		assertThrows(ModelSaveException.class, () -> modelService.save(version));
	}

	private User createUser(final String id, final String shortName) {
		final User user = modelService.create(User.class);
		user.setUid(id);
		user.setShortName(shortName);
		modelService.save(user);

		return user;
	}

	@Test
	public void testUniqueIdGenerator() {
		final User user1 = createUser(null, "user-1");
		final User user2 = createUser(null, "user-2");

		// we don't care what the id values are, important is that those values
		// were generated!

		assertNotNull(user1.getUid());
		assertEquals("user-1", user1.getShortName());

		assertNotNull(user2.getUid());
		assertEquals("user-2", user2.getShortName());
	}

	@Test
	public void testLocalizedString() {
		final Currency currency = modelService.create(Currency.class);
		currency.setIsoCode("USD");

		final String german = "german";
		final String english = "english";

		currency.setName(english, Locale.UK);
		currency.setName(german, Locale.GERMANY);

		modelService.save(currency);

		final Currency loadedCurrency = modelService.get(Currency.class, currency.getId());

		// these locales are not the same as the locales with country codes!
		Assertions.assertNull(loadedCurrency.getName(Locale.ENGLISH));
		Assertions.assertNull(loadedCurrency.getName(Locale.GERMAN));
		Assertions.assertEquals(english, loadedCurrency.getName(Locale.UK));
		Assertions.assertEquals(german, loadedCurrency.getName(Locale.GERMANY));
	}

	@Test
	public void testBidirectionalOne2ManyRelationUpdateReferenceOnChildSideWithLoadedUser() throws Exception {
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "tester1"));

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("Test street");
		user.getAddresses().add(address);

		modelService.save(user);

		final User loadedUser = modelService.get(User.class, user.getId());

		Assertions.assertEquals(1, loadedUser.getAddresses().size());
		Assertions.assertEquals(loadedUser.getAddresses().iterator().next().getId(), address.getId());
	}

	@Test
	public void testBidirectionalOne2ManyRelationUpdateReferenceOnChildSide() throws Exception {
		final User user = modelService.create(User.class);
		user.setUid("testUser1");

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("Test");
		user.getAddresses().add(address);

		Logger.debug("Addresses before save: "
				+ user.getAddresses().stream().map(a -> "ID = " + a.getId() + ", streetname = " + a.getStreetName()).collect(Collectors.joining(",")));

		modelService.save(user);

		Logger.debug("Addresses after save: "
				+ user.getAddresses().stream().map(a -> "ID = " + a.getId() + ", streetname = " + a.getStreetName()).collect(Collectors.joining(",")));

		final User loadedUser = modelService.get(User.class, user.getId());

		Logger.debug("Addresses after load: "
				+ user.getAddresses().stream().map(a -> "ID = " + a.getId() + ", streetname = " + a.getStreetName()).collect(Collectors.joining(",")));

		Assertions.assertEquals(1, loadedUser.getAddresses().size());
		Assertions.assertEquals(loadedUser.getAddresses().iterator().next().getId(), address.getId());
	}

	@Test
	public void testBidirectionalOne2ManyRelation() throws Exception {
		final User user = modelService.create(User.class);
		user.setUid("testUser2");

		final UserAddress address = modelService.create(UserAddress.class);
		address.setStreetName("Test2");
		address.setOwner(user);

		modelService.save(address);

		final User loadedUser = modelService.get(User.class, user.getId());

		Assertions.assertEquals(loadedUser.getAddresses().iterator().next().getId(), address.getId());
	}

	@Test
	public void testBidirectionalMany2ManyRelations() throws Exception {
		final UserGroup group1 = modelService.create(UserGroup.class);
		group1.setUid("testGroup 1");

		final UserGroup group2 = modelService.create(UserGroup.class);
		group2.setUid("testGroup 2");

		final User user1 = modelService.create(User.class);
		user1.setUid("testUser 1");
		user1.getGroups().add(group1);
		user1.getGroups().add(group2);

		final User user2 = modelService.create(User.class);
		user2.setUid("testUser 2");
		user2.getGroups().add(group2);

		modelService.saveAll(user1, user2);

		final User loadedUser1 = modelService.get(User.class, user1.getId());
		final User loadedUser2 = modelService.get(User.class, user2.getId());

		final UserGroup loadedGroup1 = modelService.get(UserGroup.class, group1.getId());
		final UserGroup loadedGroup2 = modelService.get(UserGroup.class, group2.getId());

		Assertions.assertEquals(2, loadedUser1.getGroups().size());
		Assertions.assertTrue(loadedUser1.getGroups().contains(loadedGroup1));
		Assertions.assertTrue(loadedUser1.getGroups().contains(loadedGroup2));

		Assertions.assertEquals(1, loadedUser2.getGroups().size());
		Assertions.assertTrue(loadedUser2.getGroups().contains(loadedGroup2));
	}

	@Test
	public void testQueryByExample() throws Exception {
		final LocalizationValue localization = modelService.create(LocalizationValue.class);
		localization.setUid("test.key");
		localization.setLocale(Locale.ENGLISH);

		modelService.save(localization);

		// test using item
		final LocalizationValue example = new LocalizationValue();
		example.setUid("test.key");
		example.setLocale(Locale.ENGLISH);

		final LocalizationValue loaded = modelService.getByExample(example);

		Assertions.assertEquals(localization.getUid(), loaded.getUid());

		// test using map
		final Map<String, Object> exampleMap = Collections.singletonMap(LocalizationValue.PROPERTY_UID, "test.key");
		final LocalizationValue resultFromMap = modelService.get(LocalizationValue.class, exampleMap);

		Assertions.assertEquals(localization.getUid(), resultFromMap.getUid());
	}

	@Test
	public void testSerialNumberGeneration() throws Exception {
		final User user = modelService.create(User.class);
		user.setShortName("test user");

		modelService.save(user);

		Assertions.assertNotNull(user.getUid());
	}

	@Test
	public void testDetachedItemIsNotSaved() {
		final User admin = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "admin"));
		admin.setShortName("detached");
		modelService.detach(admin);

		final User loadedAdmin = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "admin"));

		assertEquals("Administrator", loadedAdmin.getShortName());
	}

	@Test
	public void testValidateModelRecursively() {
//		expectedExeption.expect(ModelSaveException.class);

		// TODO: how to handle localized messages?
		// expectedExeption.expectMessage("User.id must not be null");

		final UserGroup group = modelService.create(UserGroup.class);
		group.setUid("test");

		final User user = modelService.create(User.class);
		group.getMembers().add(user);

		assertThrows(ModelSaveException.class, () -> modelService.save(group));
	}

	/**
	 * Fetch the current sequence id and compare it against the newly created user.
	 */
	@Test
	public void testUserPrepareInterceptor() {
		final User user = modelService.create(User.class);
		user.setShortName("test user");

		modelService.save(user);
		modelService.refresh(user);

		// this is the id that will be used next, so we have to add -1 to get the last used one
		long lastUsedId = sequenceGenerator.getCurrentSequenceValue(user.getTypeCode() + "_" + "id");

		// if it is 0, it was never incremented and we don't have to subtract 1
		if (lastUsedId > 0) {
			lastUsedId--;
		}

		Assertions.assertEquals(user.getTypeCode() + "-" + lastUsedId, user.getUid());
	}

	/**
	 * If the many side of a {@link OneToMany} relation has set its property to unique=true, it has to be removed when it is removed from the one-side's
	 * collection property.
	 */
	@Test
	public void testOneToManyWithUniqueConstraint() {
		final Catalog catalog = testMocker.mockCatalog();

		// this must trigger a cascade-remove on the many-side
		final CatalogVersion cvToDelete = catalog.getVersions().stream().filter(v -> "Staged".equals(v.getUid())).findFirst().get();
		catalog.getVersions().remove(cvToDelete);

		// should delete catalogversion, as the catalog is part of its unique key constraint
		modelService.save(catalog);
		modelService.refresh(catalog);

		Assertions.assertEquals(1, catalog.getVersions().size());

		// throws an exception if already deleted
		assertThrows(ModelNotFoundException.class, () -> modelService.refresh(cvToDelete));
	}

	@Disabled
	@Test
	public void testOneToManyWithUniqueConstraint_WithReference() {
		final Catalog catalog = testMocker.mockCatalog();

		// this must trigger a cascade-remove on the many-side
		// difference here is that the online catalog is referenced by the staged catalog from
		final CatalogVersion cvToDelete = catalog.getVersions().stream().filter(v -> "Online".equals(v.getUid())).findFirst().get();
		catalog.getVersions().remove(cvToDelete);

		// should delete catalogversion, as the catalog is part of its unique key constraint
		modelService.save(catalog);
		modelService.refresh(catalog);

		Assertions.assertEquals(1, catalog.getVersions().size());

		// throws an exception if already deleted
		assertThrows(ModelNotFoundException.class, () -> modelService.refresh(cvToDelete));
	}
}
