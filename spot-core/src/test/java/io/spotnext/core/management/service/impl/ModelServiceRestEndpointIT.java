package io.spotnext.core.management.service.impl;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import io.restassured.RestAssured;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.Transactionless;
import io.spotnext.itemtype.core.user.PrincipalGroup;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class ModelServiceRestEndpointIT extends AbstractIntegrationTest {

	@Value("${service.typesystem.rest.keystore.file:}")
	private String keystoreFilePath;

	@Value("${service.typesystem.rest.keystore.password:}")
	private String keystorePassword;

	@Override
	protected void prepareTest() {
		String protocol = "http";

		if (StringUtils.isNotBlank(keystoreFilePath)) {
			protocol = "https";
			RestAssured.keyStore(keystoreFilePath, keystorePassword);
			RestAssured.useRelaxedHTTPSValidation();
		}

		RestAssured.baseURI = protocol + "://localhost";
		RestAssured.port = 19000;
		RestAssured.basePath = "/v1/models";
		RestAssured.authentication = RestAssured.preemptive().basic("admin", "MD5:ee10c315eba2c75b403ea99136f5b48d");
	}

	@Override
	protected void teardownTest() {
		//
	}

	@Test
	public void test_without_authentication_fail() {
		given().auth().none() //
				.get("/country").then() //
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void get_all_models() {
		given().relaxedHTTPSValidation().get("/country").then() //
				.statusCode(HttpStatus.OK.value()) //
				.body("payload.data.size()", Matchers.greaterThan(0));
	}

	@Test
	public void get_model_of_unknown_type() {
		get("/house").then() //
				.statusCode(HttpStatus.BAD_REQUEST.value()) //
				.body("httpStatus", Matchers.equalTo("BAD_REQUEST"));
	}

	/**
	 * The {@link Transactionless} annotation is necessary, as otherwise the test method itself would be in a transaction. This would cause the REST call to
	 * fail, as it would be executed in another transaction that is not aware of the newly created item.
	 */
	@Test
	@Transactionless
	public void get_model() {
		final User user = modelService.create(User.class);
		user.setId("test-user-for-rest");
		modelService.save(user);

		sleep(1);

		get("/user/" + user.getPk()).then()//
				.statusCode(HttpStatus.OK.value()) //
				.body("payload.pk", Matchers.equalTo(user.getPk() + ""));
	}

	@Test
	public void get_unknown_model() {
		get("/user/" + "200000000").then() //
				.statusCode(HttpStatus.NOT_FOUND.value()) //
				.body("httpStatus", Matchers.equalTo("NOT_FOUND"));
	}

	@Test
	public void queryModel() {
		get("/user/query/?q=id like '%test%'").then() //
				.statusCode(HttpStatus.OK.value()) //
				.body("payload.results.size()", Matchers.greaterThan(0));
	}

	@Test
	public void queryModelByExample() throws JSONException {
		final JSONObject example = new JSONObject().put("id", "tester51");

		given().body(example.toString())
				.post("/user/query/").then() //
				.statusCode(HttpStatus.OK.value()) //
				.body("payload[0].id", Matchers.equalTo("tester51"));
	}

	@Test
	public void createModel() throws JSONException {
		User tester1 = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester1"));

		JSONArray groups = new JSONArray();

		for (PrincipalGroup g : tester1.getGroups()) {
			JSONObject gr = new JSONObject();
			gr.put("pk", g.getPk());
			gr.put("typeCode", g.getTypeCode());
			groups.put(gr);
		}

		final JSONObject newUser = new JSONObject() //
				.put("id", "user@integrationtest") //
				.put("groups", groups);

		final Long pk = Long.valueOf(given().body(newUser.toString())
				.post("/user").then() //
				.statusCode(HttpStatus.CREATED.value()) //
				.body("payload.pk", Matchers.notNullValue()).extract().jsonPath().get("payload.pk"));

		assertNotNull(pk);

		final User user = modelService.get(User.class, pk);

		assertEquals(tester1.getGroups().size(), user.getGroups().size());
		assertEquals(tester1.getGroups().iterator().next().getId(), user.getGroups().iterator().next().getId());

		assertNotNull(user);
	}

	@Test
	public void createOrUpdateModel_WithPK() throws JSONException {
		// updates existing user
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester51"));
		final UserGroup usergroup = modelService.get(UserGroup.class, Collections.singletonMap(User.PROPERTY_ID, "employee-group"));

		final JSONObject group = new JSONObject();
		group.put("pk", usergroup.getPk() + "");
		group.put("typeCode", usergroup.getTypeCode());

		final JSONArray groups = new JSONArray();
		groups.put(group);

		final JSONObject shortNameUpdate = new JSONObject() //
				.put("shortName", "integrationtester")//
				.put("pk", user.getPk() + "")
				.put("groups", groups);

		given().body(shortNameUpdate.toString())
				.put("/user").then() //
				.statusCode(HttpStatus.ACCEPTED.value());

		modelService.refresh(user);

		assertEquals("integrationtester", user.getShortName());
		assertEquals(usergroup.getPk(), user.getGroups().iterator().next().getPk());
		assertEquals("MD5:16d7a4fca7442dda3ad93c9a726597e4", user.getPassword());
	}

	@Test
	public void createOrUpdateModel_WithoutPK() throws JSONException {
		final JSONObject shortNameUpdate = new JSONObject().put("shortName", "integrationtester");

		final Long pk = Long.valueOf(given().body(shortNameUpdate.toString())
				.put("/user").then() //
				.statusCode(HttpStatus.CREATED.value())
				.body("payload.pk", Matchers.notNullValue()).extract().jsonPath().get("payload.pk"));

		final User user = modelService.get(User.class, pk);

		assertEquals("integrationtester", user.getShortName());
		assertEquals(0, user.getGroups().size());
	}

	@Test
	public void partiallyUpdateModel() throws JSONException {
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_ID, "tester51"));

		final JSONObject shortNameUpdate = new JSONObject().put("shortName", "integrationtester");

		given().body(shortNameUpdate.toString())
				.patch("/user/" + user.getPk()).then() //
				.statusCode(HttpStatus.ACCEPTED.value());

		modelService.refresh(user);

		assertEquals(user.getShortName(), "integrationtester");
	}

}
