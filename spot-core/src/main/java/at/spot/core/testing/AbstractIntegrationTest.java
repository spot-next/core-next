package at.spot.core.testing;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import at.spot.core.CoreInit;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;

/**
 * This is the base class for all integration tasks..
 */
@RunWith(SpotJunitRunner.class)
@Test
@SpringBootTest(classes = { CoreInit.class })
public abstract class AbstractIntegrationTest {

	@Resource
	protected LoggingService loggingService;

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

	@Before
	public void beforeTest() {
		// start transaction
		try {
			prepareTest();
		} catch (Exception e) {
			loggingService.exception(String.format("Could not prepare test %s", this.getClass().getName()), e);
		}
	}

	/**
	 * 
	 */
	@After
	public void afterTest() {
		// revert transaction
		try {
			teardownTest();
		} catch (Exception e) {
			loggingService.exception(String.format("Could not teardown test %s", this.getClass().getName()), e);
		}
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
