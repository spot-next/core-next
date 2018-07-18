package at.spot.core.infrastructure.strategy.impl;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.persistence.query.LambdaQuery;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.infrastructure.exception.ImpexImportException;
import at.spot.core.infrastructure.strategy.ImpexImportStrategy;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.beans.ImportConfiguration;
import at.spot.itemtype.core.media.Media;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

public class DefaultImpexImportStrategyIT extends AbstractIntegrationTest {

	@Resource
	private QueryService queryService;

	@Resource
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
	public void testInsertNestedReference() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/nested_reference.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		final LambdaQuery<Media> query = new LambdaQuery<>(Media.class).filter(u -> u.getId().equals("testMedia"));
		final QueryResult<Media> result = queryService.query(query);

		Assert.assertTrue(result.count() == 1);
	}

	@Test
	public void testInsertMultipleItemsNoRelationImportImpex() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/multiple_items_no_relations.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		final LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testuser"));
		final QueryResult<User> userResult = queryService.query(userQuery);

		Assert.assertTrue(userResult.count() == 1);
		Assert.assertEquals("testuser", userResult.getResultList().get(0).getId());

		final LambdaQuery<UserGroup> userGroupQuery = new LambdaQuery<>(UserGroup.class)
				.filter(u -> u.getId().equals("test-group"));
		final QueryResult<UserGroup> userGroupResult = queryService.query(userGroupQuery);

		Assert.assertTrue(userGroupResult.count() == 1);
		Assert.assertEquals("test-group", userGroupResult.getResultList().get(0).getId());
	}

	@Test
	public void testInsertMultipleItemsWithRelationImportImpex() throws ImpexImportException {
		final ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/multiple_items_with_relations.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		final LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testuser"));
		final QueryResult<User> userResult = queryService.query(userQuery);

		Assert.assertTrue(userResult.count() == 1);
		Assert.assertEquals("testuser", userResult.getResultList().get(0).getId());

		final LambdaQuery<UserGroup> userGroupQuery = new LambdaQuery<>(UserGroup.class)
				.filter(u -> u.getId().equals("test-group"));
		final QueryResult<UserGroup> userGroupResult = queryService.query(userGroupQuery);

		Assert.assertTrue(userGroupResult.count() == 1);
		Assert.assertEquals("test-group", userGroupResult.getResultList().get(0).getId());

		// check if user is in group
		Assert.assertEquals("testuser", userGroupResult.getResultList().get(0).getMembers().iterator().next().getId());
	}

	// INSERT_UPDATE
	@Test
	public void testInsertUpdateNestedReference() throws ImpexImportException {
		//
	}

	// UPDATE
	@Test
	public void testUpdateNestedReference() throws ImpexImportException {
		//
	}

	// REMOVE
	@Test
	public void testRemoveNestedReference() throws ImpexImportException {
		// import test media
		ImportConfiguration conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/nested_reference.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		// check if test data media is there
		LambdaQuery<Media> query = new LambdaQuery<>(Media.class).filter(u -> u.getId().equals("testMedia"));
		QueryResult<Media> result = queryService.query(query);

		Assert.assertTrue(result.count() == 1);

		// and them remove it again
		conf = new ImportConfiguration();
		conf.setScriptIdentifier("/data/test/remove_nested_reference.impex");
		impexImportStrategy.importImpex(conf, getClass().getResourceAsStream(conf.getScriptIdentifier()));

		// and check if it's realy gone
		query = new LambdaQuery<>(Media.class).filter(u -> u.getId().equals("testMedia"));
		result = queryService.query(query);

		Assert.assertTrue(result.count() == 0);
	}
}
