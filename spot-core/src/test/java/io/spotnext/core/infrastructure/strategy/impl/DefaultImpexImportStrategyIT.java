package io.spotnext.core.infrastructure.strategy.impl;

import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.exception.ImpexImportException;
import io.spotnext.core.infrastructure.strategy.ImpexImportStrategy;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.beans.ImportConfiguration;
import io.spotnext.itemtype.core.internationalization.Country;
import io.spotnext.itemtype.core.media.Media;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class DefaultImpexImportStrategyIT extends AbstractIntegrationTest {

	@Autowired
	private QueryService queryService;

	@Autowired
	private ImpexImportStrategy impexImportStrategy;

	@Override
	protected void prepareTest() {
		//
	}

	@Override
	protected void teardownTest() {
		//
	}

	// INSERT

	@Test
	public void testLocalizedString() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/localized_string.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		final LambdaQuery<Country> query = new LambdaQuery<>(Country.class).filter(c -> c.getIso3Code().equals("TES"));
		final QueryResult<Country> result = queryService.query(query);

		final Country country = result.getResultList().get(0);

		Assert.assertEquals("test land", country.getShortName(Locale.ENGLISH));
		Assert.assertEquals("Testland", country.getShortName(Locale.GERMANY));
	}

	@Test
	public void testInsertNestedReference() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/nested_reference.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		final LambdaQuery<Media> query = new LambdaQuery<>(Media.class).filter(u -> u.getUid().equals("testMedia"));
		final QueryResult<Media> result = queryService.query(query);

		Assert.assertTrue(result.getTotalCount() == 1);
	}

	@Test
	public void testInsertMultipleItemsNoRelationImportImpex() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/multiple_items_no_relations.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		final LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getUid().equals("testuser"));
		final QueryResult<User> userResult = queryService.query(userQuery);

		Assert.assertTrue(userResult.getTotalCount() == 1);
		Assert.assertEquals("testuser", userResult.getResultList().get(0).getUid());

		final LambdaQuery<UserGroup> userGroupQuery = new LambdaQuery<>(UserGroup.class).filter(u -> u.getUid().equals("test-group"));
		final QueryResult<UserGroup> userGroupResult = queryService.query(userGroupQuery);

		Assert.assertTrue(userGroupResult.getTotalCount() == 1);
		Assert.assertEquals("test-group", userGroupResult.getResultList().get(0).getUid());
	}

	@Test
	public void testInsertMultipleItemsWithRelationImportImpex() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/multiple_items_with_relations.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		// query user and user group
		final LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getUid().equals("testuser"));
		final QueryResult<User> userResult = queryService.query(userQuery);
		final LambdaQuery<UserGroup> userGroupQuery = new LambdaQuery<>(UserGroup.class).filter(u -> u.getUid().equals("test-group"));
		userGroupQuery.setIgnoreCache(true);
		final QueryResult<UserGroup> userGroupResult = queryService.query(userGroupQuery);

		Assert.assertTrue(userResult.getTotalCount() == 1);
		Assert.assertEquals("testuser", userResult.getResultList().get(0).getUid());
		Assert.assertTrue(userGroupResult.getTotalCount() == 1);
		Assert.assertEquals("test-group", userGroupResult.getResultList().get(0).getUid());

		// check if user is in group
		Assert.assertEquals("testuser", userGroupResult.getResultList().get(0).getMembers().iterator().next().getUid());
	}

	// INSERT_UPDATE
	@Test
	public void testInsertUpdateNestedReference() throws ImpexImportException {
		//
	}

	// UPDATE
	@Test
	public void testUpdateByUid() throws ImpexImportException {
		final String groupId = "employee-group";
		final String groupShortName = "Employee Group (fixed)";

		// check if group has no shortName
		LambdaQuery<UserGroup> query = new LambdaQuery<>(UserGroup.class).filter(u -> u.getUid().equals(groupId));
		QueryResult<UserGroup> result = queryService.query(query);

		Assert.assertNull(result.getResultList().get(0).getShortName());

		// execute update
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/update_employee_group.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		// check if data has been updated
		query = new LambdaQuery<>(UserGroup.class).filter(u -> u.getUid().equals(groupId));
		result = queryService.query(query);

		Assert.assertEquals(groupShortName, result.getResultList().get(0).getShortName());
		Assert.assertTrue(CollectionUtils.isEmpty(result.getResultList().get(0).getGroups()));
	}

	// REMOVE
	@Test
	public void testRemoveNestedReference() throws ImpexImportException {
		// import test media
		ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/nested_reference.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		// check if test data media is there
		LambdaQuery<Media> query = new LambdaQuery<>(Media.class).filter(u -> u.getUid().equals("testMedia"));
		QueryResult<Media> result = queryService.query(query);

		Assert.assertTrue(result.getTotalCount() == 1);

		// and them remove it again
		conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/remove_nested_reference.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		// and check if it's realy gone
		query = new LambdaQuery<>(Media.class).filter(u -> u.getUid().equals("testMedia"));
		result = queryService.query(query);

		Assert.assertTrue(result.getTotalCount() == 0);
	}
}
