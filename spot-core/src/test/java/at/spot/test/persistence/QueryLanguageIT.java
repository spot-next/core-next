package at.spot.test.persistence;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.query.lambda.LambdaQuery;
import at.spot.core.persistence.query.lambda.SerializablePredicate;

import at.spot.core.persistence.service.QueryService;
import at.spot.core.testing.AbstractIntegrationTest;
import at.spot.itemtype.core.user.User;

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

		modelService.save(user);
	}

	@Override
	protected void teardownTest() {

	}

	@Test
	public void testSimpleItemTypeQuery() throws Exception {
		Query<User> query = new Query<>("SELECT u FROM User u WHERE id = :id", User.class);
		query.addParam("id", "testUser");
		QueryResult<User> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0).getId(), user.getId());
	}

	@Test
	public void testSimpleTypeQuery() throws Exception {
		Query<String> query = new Query<>("SELECT id FROM User u WHERE id = :id", String.class);
		query.addParam("id", "testUser");
		QueryResult<String> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0), user.getId());
	}

	@Test
	public void testDtoQuery() throws Exception {
		Query<UserData> query = new Query<>("SELECT id as id, shortName as shortName FROM User u WHERE id = :id",
				UserData.class);
		query.addParam("id", "testUser");
		QueryResult<UserData> result = queryService.query(query);

		Assert.assertEquals(result.getResultList().get(0).getId(), user.getId());
		Assert.assertEquals(result.getResultList().get(0).getShortName(), user.getShortName());
	}

	@Test
	public void testLamdbaQuery() throws Exception {
		final SerializablePredicate<User> pred = u -> u.getId().equals("testUser");
		final LambdaQuery<User> query = new LambdaQuery<>(User.class).filter(pred);
		QueryResult<User> result = queryService.query(query);

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
