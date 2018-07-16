package at.spot.core.infrastructure.strategy.impl;

import java.io.File;

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void teardownTest() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testNestedReference() throws ImpexImportException {
		impexImportStrategy.importImpex(new ImportConfiguration(),
				new File(getClass().getResource("/data/test/nested_reference.impex").getFile()));

		LambdaQuery<Media> query = new LambdaQuery<>(Media.class).filter(u -> u.getId().equals("testMedia"));
		QueryResult<Media> result = queryService.query(query);

		Assert.assertTrue(result.count() == 1);
	}

	@Test
	public void testMultipleItemsNoRelationImportImpex() throws ImpexImportException {
		impexImportStrategy.importImpex(new ImportConfiguration(),
				new File(getClass().getResource("/data/test/multiple_items_no_relations.impex").getFile()));

		LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testuser"));
		QueryResult<User> userResult = queryService.query(userQuery);

		Assert.assertTrue(userResult.count() == 1);
		Assert.assertEquals("testuser", userResult.getResultList().get(0).getId());

		LambdaQuery<UserGroup> userGroupQuery = new LambdaQuery<>(UserGroup.class)
				.filter(u -> u.getId().equals("test-group"));
		QueryResult<UserGroup> userGroupResult = queryService.query(userGroupQuery);

		Assert.assertTrue(userGroupResult.count() == 1);
		Assert.assertEquals("test-group", userGroupResult.getResultList().get(0).getId());
	}

	@Test
	public void testMultipleItemsWithRelationImportImpex() throws ImpexImportException {
		impexImportStrategy.importImpex(new ImportConfiguration(),
				new File(getClass().getResource("/data/test/multiple_items_with_relations.impex").getFile()));

		LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testuser"));
		QueryResult<User> userResult = queryService.query(userQuery);

		Assert.assertTrue(userResult.count() == 1);
		Assert.assertEquals("testuser", userResult.getResultList().get(0).getId());

		LambdaQuery<UserGroup> userGroupQuery = new LambdaQuery<>(UserGroup.class)
				.filter(u -> u.getId().equals("test-group"));
		QueryResult<UserGroup> userGroupResult = queryService.query(userGroupQuery);

		Assert.assertTrue(userGroupResult.count() == 1);
		Assert.assertEquals("test-group", userGroupResult.getResultList().get(0).getId());

		// check if user is in group
		Assert.assertEquals("testuser", userGroupResult.getResultList().get(0).getMembers().iterator().next().getId());
	}

}
