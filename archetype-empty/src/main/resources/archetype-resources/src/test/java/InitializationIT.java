package $package;

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.spotnext.core.CoreInit;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.core.testing.IntegrationTest;
import io.spotnext.instrumentation.DynamicInstrumentationLoader;
import ${package}.Init;

/**
 * This integration test checks if the module has been initialized properly.
 */
@IntegrationTest(initClass = Init.class)
@SpringBootTest(classes = { Init.class, CoreInit.class })
public class InitializationIT extends AbstractIntegrationTest {

	@Resource
	Init init;

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
		assertTrue(Init.class.getSimpleName() + " not initialized", init.isAlreadyInitialized());
		assertTrue("Instrumentation not initiazed", DynamicInstrumentationLoader.isInstrumentationAvailable());
	}

}
