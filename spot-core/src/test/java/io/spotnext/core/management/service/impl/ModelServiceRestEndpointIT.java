package io.spotnext.core.management.service.impl;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import io.restassured.RestAssured;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.Transactionless;
import io.spotnext.itemtype.core.user.User;

public class ModelServiceRestEndpointIT extends AbstractIntegrationTest {

	@Override
	protected void prepareTest() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 19000;
		RestAssured.basePath = "/v1/models";
		RestAssured.authentication = RestAssured.preemptive().basic("admin", "MD5:ee10c315eba2c75b403ea99136f5b48d");
	}

	@Override
	protected void teardownTest() {
		//
	}

	@Test
	public void get_all_models() {
		get("/country").then() //
				.statusCode(200) //
				.body("payload.data.size()", Matchers.greaterThan(0));
	}

	@Test
	public void get_model_of_unknown_type() {
		get("/house").then() //
				.statusCode(400) //
				.body("httpStatus", Matchers.equalTo("BAD_REQUEST"));
	}

	/**
	 * The {@link Transactionless} annotation is necessary, as otherwise the test method itself would be in a transaction. This would cause the REST call to
	 * fail, as it would be executed in another transaction that is not aware of the newly created item.
	 */
	@Test
	@Transactionless
	public void get_model() {
		User user = modelService.create(User.class);
		user.setId("test-user-for-rest");
		modelService.save(user);

		sleep(1);

		get("/user/" + user.getPk()).then()//
				.statusCode(200) //
				.body("payload.pk", Matchers.equalTo(user.getPk()));
	}

	@Test
	public void get_unknown_model() {
		get("/user/" + "200000000").then() //
				.statusCode(404) //
				.body("httpStatus", Matchers.equalTo("NOT_FOUND"));
	}

	@Test
	public void queryModel() {
		get("/user/query/?q=id like '%test%'").then() //
				.statusCode(200) //
				.body("payload.results.size()", Matchers.greaterThan(0));
	}

	@Test
	public void queryModelByExample() throws JSONException {
		JSONObject example = new JSONObject().put("id", "tester51");

		given().body(example.toString())
				.post("/user/query/").then() //
				.statusCode(200) //
				.body("payload[0].id", Matchers.equalTo("tester51"));
	}

	@Test
	public void createModel() throws JSONException {
		JSONObject newUser = new JSONObject().put("id", "user@integrationtest");

		Long pk = given().body(newUser.toString())
				.post("/user").then() //
				.statusCode(201) //
				.body("payload.pk", Matchers.notNullValue()).extract().jsonPath().get("payload.pk");

		assertNotNull(pk);

		User user = modelService.get(User.class, pk);

		assertNotNull(user);
	}

	@Ignore
	@Test
	public void createOrUpdateModel() throws JSONException {
		// updates existing user
		User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester51"));

		JSONObject shortNameUpdate = new JSONObject().put("shortName", "integrationtester");

		given().body(shortNameUpdate.toString())
				.put("/user/" + user.getPk()).then() //
				.statusCode(202);

		modelService.refresh(user);

		assertEquals(user.getShortName(), "integrationtester");
		assertEquals(1, user.getGroups().size());
		assertEquals("MD5:16d7a4fca7442dda3ad93c9a726597e4", user.getPassword());
		
	}

	@Test
	public void partiallyUpdateModel() throws JSONException {
		User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester51"));

		JSONObject shortNameUpdate = new JSONObject().put("shortName", "integrationtester");

		given().body(shortNameUpdate.toString())
				.patch("/user/" + user.getPk()).then() //
				.statusCode(202);

		modelService.refresh(user);

		assertEquals(user.getShortName(), "integrationtester");
	}

}
