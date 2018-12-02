package io.spotnext.core.management.service.impl;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.restassured.RestAssured;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.strategy.impl.DefaultJsonSerializationStrategy;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.Transactionless;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;
import io.spotnext.itemtype.core.user.PrincipalGroup;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class ModelServiceRestEndpointIT extends AbstractIntegrationTest {
	@Autowired
	DefaultJsonSerializationStrategy serializer;

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
	public void testWithoutAuthenticationFail() {
		given().auth().none() //
				.get("/country").then() //
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void testGetAllModels() {
		given().relaxedHTTPSValidation().get("/country").then() //
				.statusCode(HttpStatus.OK.value()) //
				.body("payload.data.size()", Matchers.greaterThan(0));
	}

	@Test
	public void testGetModelOfUnknownType() {
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
	public void testGetModel() {
		final User user = modelService.create(User.class);
		user.setUid("test-user-for-rest");
		modelService.save(user);

		sleep(1);

		get("/user/" + user.getId()).then()//
				.statusCode(HttpStatus.OK.value()) //
				.body("payload.id", Matchers.equalTo(user.getId() + ""));
	}

	@Test
	public void testGetUnknownModel() {
		get("/user/" + "200000000").then() //
				.statusCode(HttpStatus.NOT_FOUND.value()) //
				.body("httpStatus", Matchers.equalTo("NOT_FOUND"));
	}

	@Test
	public void testQueryModel() {
		get("/user/query/?q=uid like '%test%'").then() //
				.statusCode(HttpStatus.OK.value()) //
				.body("payload.results.size()", Matchers.greaterThan(0));
	}

	@Test
	public void testQueryModelByExample() throws JSONException {
		final JSONObject example = new JSONObject().put("uid", "tester51");

		given().body(example.toString())
				.post("/user/query/").then() //
				.statusCode(HttpStatus.OK.value()) //
				.body("payload[0].uid", Matchers.equalTo("tester51"));
	}

	@Test
	public void testCreateModel() throws JSONException {
		User tester1 = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "tester1"));

		JSONArray groups = new JSONArray();

		for (PrincipalGroup g : tester1.getGroups()) {
			JSONObject gr = new JSONObject();
			gr.put("id", g.getId());
			gr.put("typeCode", g.getTypeCode());
			groups.put(gr);
		}

		final JSONObject newUser = new JSONObject() //
				.put("uid", "user@integrationtest") //
				.put("groups", groups);

		final Long id = Long.valueOf(given().body(newUser.toString())
				.post("/user").then() //
				.statusCode(HttpStatus.CREATED.value()) //
				.body("payload.id", Matchers.notNullValue()).extract().jsonPath().get("payload.id"));

		assertNotNull(id);

		final User user = modelService.get(User.class, id);

		assertEquals(tester1.getGroups().size(), user.getGroups().size());
		assertEquals(tester1.getGroups().iterator().next().getUid(), user.getGroups().iterator().next().getUid());

		assertNotNull(user);
	}

	@Test
	public void testCreateOrUpdateModel_WithID() throws JSONException {
		// updates existing user
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "tester51"));
		final UserGroup usergroup = modelService.get(UserGroup.class, Collections.singletonMap(User.PROPERTY_UID, "employee-group"));

		final JSONObject group = new JSONObject();
		group.put("id", usergroup.getId() + "");
		group.put("typeCode", usergroup.getTypeCode());

		final JSONArray groups = new JSONArray();
		groups.put(group);

		final JSONObject shortNameUpdate = new JSONObject() //
				.put("shortName", "integrationtester")//
				.put("id", user.getId() + "")
				.put("groups", groups);

		given().body(shortNameUpdate.toString())
				.put("/user").then() //
				.statusCode(HttpStatus.ACCEPTED.value());

		modelService.refresh(user);

		assertEquals("integrationtester", user.getShortName());
		assertEquals(usergroup.getId(), user.getGroups().iterator().next().getId());
		assertEquals("MD5:16d7a4fca7442dda3ad93c9a726597e4", user.getPassword());
	}

	@Test
	public void testCreateOrUpdateModel_WithoutID() throws JSONException {
		final JSONObject shortNameUpdate = new JSONObject().put("shortName", "integrationtester");

		final Long id = Long.valueOf(given().body(shortNameUpdate.toString())
				.put("/user").then() //
				.statusCode(HttpStatus.CREATED.value())
				.body("payload.id", Matchers.notNullValue()).extract().jsonPath().get("payload.id"));

		final User user = modelService.get(User.class, id);

		assertEquals("integrationtester", user.getShortName());
		assertEquals(0, user.getGroups().size());
	}

	@Test
	public void testartiallyUpdateModel() throws JSONException {
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "tester51"));

		final JSONObject shortNameUpdate = new JSONObject().put("shortName", "integrationtester");

		given().body(shortNameUpdate.toString())
				.patch("/user/" + user.getId()).then() //
				.statusCode(HttpStatus.ACCEPTED.value());

		modelService.refresh(user);

		assertEquals(user.getShortName(), "integrationtester");
	}

	@Test
	public void testUpdateOneToManySideWithUniqueConstraint() {
		Catalog mediaCatalog = modelService.get(Catalog.class, Collections.singletonMap("uid", "Media"));

		List<Long> catalogVersionIds = mediaCatalog.getVersions().stream().map(cv -> cv.getId()).collect(Collectors.toList());

		// detach and remove one catalog version
		modelService.detach(mediaCatalog);
		mediaCatalog.getVersions().remove(mediaCatalog.getVersions().iterator().next());

		String json = serializer.serialize(mediaCatalog);

		given().body(json)
				.put("/catalog").then() //
				.statusCode(HttpStatus.ACCEPTED.value());

		byte exceptionCount = 0;

		// one of the catalog versions cannot be refreshed, as it should have been cascade-removed when removed from the catalog's versions collection.
		for (long id : catalogVersionIds) {
			try {
				modelService.get(CatalogVersion.class, id);
			} catch (ModelNotFoundException e) {
				exceptionCount++;
			}
		}

		modelService.refresh(mediaCatalog);

		assertEquals(1, mediaCatalog.getVersions().size());

		// check that only one exception is thrown because of the removed catalogVersion
//		assertEquals(1, exceptionCount);
	}
}
