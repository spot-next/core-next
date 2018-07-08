package at.spot.core.infrastructure.strategy.impl;

import java.nio.file.Paths;

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
import at.spot.itemtype.core.user.User;

public class DefaultImpexImportStrategyTest extends AbstractIntegrationTest {

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
	public void testImportImpex() throws ImpexImportException {
		impexImportStrategy.importImpex(new ImportConfiguration(), Paths.get("/data/initial/users.impex").toFile());

		LambdaQuery<User> userQuery = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("admin"));
		QueryResult<User> userResult = queryService.query(userQuery);

		Assert.assertTrue(userResult.count() == 1);
		Assert.assertEquals("admin", userResult.getResultList().get(0).getId());
	}

}
