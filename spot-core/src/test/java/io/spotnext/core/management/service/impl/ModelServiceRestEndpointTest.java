package io.spotnext.core.management.service.impl;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.get;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.restassured.RestAssured;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.user.User;

public class ModelServiceRestEndpointTest extends AbstractIntegrationTest {

	@Override
	protected void prepareTest() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 19000;
		RestAssured.basePath = "/v1/models";
		RestAssured.authentication = basic("admin", "MD5:ee10c315eba2c75b403ea99136f5b48d");
	}

	@Override
	protected void teardownTest() {
		//
	}

	@Test
	public void get_all_models() {
		get("/country").then()//
				.statusCode(200) //
				.body("payload.data.size()", Matchers.greaterThan(0));
	}

	@Test
	public void get_model_of_unknown_type() {
		get("/house").then()//
				.statusCode(400) //
				.body("httpStatus", Matchers.equalTo("BAD_REQUEST"));
	}

	@Test
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
		get("/user/" + "200000000").then()//
				.statusCode(404) //
				.body("httpStatus", Matchers.equalTo("NOT_FOUND"));
	}

	@Test
	public void queryModel() {

	}

	@Test
	public void queryModelByExample() {

	}

	@Test
	public void createModel() {

	}

	@Test
	public void createOrUpdateModel() {

	}

	@Test
	public void partiallyUpdateModel() {

	}

}
