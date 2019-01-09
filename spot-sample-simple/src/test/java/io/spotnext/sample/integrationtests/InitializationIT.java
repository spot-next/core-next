package io.spotnext.sample.integrationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.IntegrationTest;
import io.spotnext.instrumentation.DynamicInstrumentationLoader;
import io.spotnext.itemtype.core.internationalization.Language;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.sample.SampleInit;

/**
 * This integration test checks if the module has been initialized properly.
 */
@IntegrationTest(initClass = SampleInit.class)
@SpringBootTest(classes = { SampleInit.class, CoreInit.class })
public class InitializationIT extends AbstractIntegrationTest {

	@Autowired
	SampleInit init;

	@Autowired
	ModelService modelService;

	@Override
	protected void prepareTest() {
		//
	}

	@Override
	protected void teardownTest() {
		//
	}

	@Test
	public void moduleInitialized() {
		assertTrue(SampleInit.class.getSimpleName() + " not initialized", init.isAlreadyInitialized());
		assertTrue("Instrumentation not initiazed", DynamicInstrumentationLoader.isInstrumentationAvailable());

		// make sure the item type transformer kicks it by referencing an item type
		User user = new User();

		// check if the core initial data have been imported correctly
		Language english = modelService.get(new ModelQuery<>(Language.class, Collections.singletonMap("iso3Code", "eng")));

		assertNotNull(english);
	}

}
