package at.spot.core.testing;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.support.init.ModuleInit;

/**
 * This is the base class for all integration tasks..
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public abstract class AbstractIntegrationTest {

	@Resource
	protected ModelService modelService;

	/**
	 * This is the entry point of the integration test. It starts up spOt and then
	 * executes the test.
	 */
	// @BeforeClass
	// public static void boot() {
	// Bootstrap.bootstrap(getInitClass(), new String[] { getTestPackagePath() });
	// }

	protected String getTestPackagePath() {
		return this.getClass().getPackage().getName();
	}

	protected abstract Class<? extends ModuleInit> getInitClass();

	@Before
	public void beforeTest() {
		// start transaction
		prepareTest();
	}

	/**
	 * 
	 */
	@After
	public void afterTest() {
		// revert transaction
		teardownTest();
	}

	/**
	 * Runs custom code before a test is executed, eg. to prepare test data.
	 */
	protected abstract void prepareTest();

	/**
	 * Runs after each test, eg. to clean up stuff.
	 */
	protected abstract void teardownTest();
}
