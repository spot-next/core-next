package io.spotnext.test.persistence;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class QueryLanguageIT extends AbstractIntegrationTest {

	@Autowired
	QueryService queryService;

	User user;

	@Override
	protected void prepareTest() {
		user = modelService.create(User.class);
		user.setUid("testUser");
		user.setEmailAddress("test@test.at");
		user.setPassword("test1234");
		user.setShortName("tester");

		UserGroup group = modelService.create(UserGroup.class);
		group.setUid("testGroup");
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

		Assert.assertTrue(result.getResults().size() > 0);
	}

	@Test
	public void testSimpleItemTypeQuery() throws Exception {
		final JpqlQuery<User> query = new JpqlQuery<>("SELECT u FROM User u WHERE uid = :uid", User.class);
		query.addParam("uid", "testUser");
		final QueryResult<User> result = queryService.query(query);

		Assert.assertEquals(result.getResults().get(0).getUid(), user.getUid());
	}

	@Test
	public void testSimpleTypeQuery() throws Exception {
		final JpqlQuery<String> query = new JpqlQuery<>("SELECT uid FROM User u WHERE uid = :uid", String.class);
		query.addParam("uid", "testUser");
		final QueryResult<String> result = queryService.query(query);

		Assert.assertEquals(result.getResults().get(0), user.getUid());
	}

	@Test
	public void testDtoQuery() throws Exception {
		final JpqlQuery<UserData> query = new JpqlQuery<>(
				"SELECT uid as uid, shortName as shortName FROM User u WHERE uid = :uid", UserData.class);
		query.addParam("uid", "testUser");
		final QueryResult<UserData> result = queryService.query(query);

		Assert.assertEquals(result.getResults().get(0).getUid(), user.getUid());
		Assert.assertEquals(result.getResults().get(0).getShortName(), user.getShortName());
	}

	// not yet working as the column name is not automatically used as alias.
	@Ignore
	@Test
	public void testDtoQueryWithoutAlias() throws Exception {
		final JpqlQuery<UserData> query = new JpqlQuery<>("SELECT uid, shortName FROM User u WHERE uid = :uid",
				UserData.class);
		query.addParam("uid", "testUser");
		final QueryResult<UserData> result = queryService.query(query);

		Assert.assertEquals(result.getResults().get(0).getUid(), user.getUid());
		Assert.assertEquals(result.getResults().get(0).getShortName(), user.getShortName());
	}

	@Test
	public void testLamdbaQuery() throws Exception {
		final LambdaQuery<User> query = new LambdaQuery<>(User.class).filter(u -> u.getUid().equals("testUser"));
		final QueryResult<User> result = queryService.query(query);

		Assert.assertEquals(result.getResults().get(0).getUid(), user.getUid());
		Assert.assertEquals(result.getResults().get(0).getShortName(), user.getShortName());
	}

	protected static class UserData {
		private String uid;
		private String shortName;

		public String getUid() {
			return uid;
		}

		public void getUid(String uid) {
			this.uid = uid;
		}

		public String getShortName() {
			return shortName;
		}

		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
	}

}
