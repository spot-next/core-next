package at.spot.test.persistence;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.LambdaQuery;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.persistence.service.QueryService;
import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

public class QueryLanguageIT extends AbstractIntegrationTest {

	@Resource
	QueryService queryService;

	User user;

	@Override
	protected void prepareTest() {
		user = modelService.create(User.class);
		user.setId("testUser");
		user.setEmailAddress("test@test.at");
		user.setPassword("test1234");
		user.setShortName("tester");

		UserGroup group = modelService.create(UserGroup.class);
		group.setId("testGroup");
		group.setShortName("test-group");

		group.getMembers().add(user);

		modelService.save(group);
	}

	@Override
	protected void teardownTest() {

	}

	@Test
	public void testFetchSubGraph() throws Exception {
		final JpqlQuery<UserGroup> query = new JpqlQuery<>("SELECT u FROM UserGroup u", UserGroup.class);
		query.setEagerFetchRelations(true);
		final QueryResult<UserGroup> result = queryService.query(query);

		Assert.assertTrue(result.getResultList().size() > 0);
	}

	@Test
	public void testSimpleItemTypeQuery() throws Exception {
		final JpqlQuery<User> query = new JpqlQuery<>("SELECT u FROM User u WHERE id = :id", User.class);
		query.addParam("id", "testUser");
		final QueryResult<User> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0).getId(), user.getId());
	}

	@Test
	public void testSimpleTypeQuery() throws Exception {
		final JpqlQuery<String> query = new JpqlQuery<>("SELECT id FROM User u WHERE id = :id", String.class);
		query.addParam("id", "testUser");
		final QueryResult<String> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0), user.getId());
	}

	@Test
	public void testDtoQuery() throws Exception {
		final JpqlQuery<UserData> query = new JpqlQuery<>(
				"SELECT id as id, shortName as shortName FROM User u WHERE id = :id", UserData.class);
		query.addParam("id", "testUser");
		final QueryResult<UserData> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0).getId(), user.getId());
		Assert.assertEquals(result.getResultList().get(0).getShortName(), user.getShortName());
	}

	// not yet working as the column name is not automatically used as alias.
	@Ignore
	@Test
	public void testDtoQueryWithoutAlias() throws Exception {
		final JpqlQuery<UserData> query = new JpqlQuery<>("SELECT id, shortName FROM User u WHERE id = :id",
				UserData.class);
		query.addParam("id", "testUser");
		final QueryResult<UserData> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0).getId(), user.getId());
		Assert.assertEquals(result.getResultList().get(0).getShortName(), user.getShortName());
	}

	@Test
	public void testLamdbaQuery() throws Exception {
		final LambdaQuery<User> query = new LambdaQuery<>(User.class).filter(u -> u.getId().equals("testUser"));
		final QueryResult<User> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0).getId(), user.getId());
		Assert.assertEquals(result.getResultList().get(0).getShortName(), user.getShortName());
	}

	protected static class UserData {
		private String id;
		private String shortName;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getShortName() {
			return shortName;
		}

		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
	}

}
