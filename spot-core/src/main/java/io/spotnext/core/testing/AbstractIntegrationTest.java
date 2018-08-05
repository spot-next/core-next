package io.spotnext.core.testing;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.persistence.service.TransactionService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is the base class for all integration tasks. Database access will be
 * reverted after the each test using a transaction rollback.
 */
@TestPropertySource(locations = "classpath:/core-testing.properties")
@RunWith(SpotJunitRunner.class)
@IntegrationTest
@SpringBootTest(classes = { CoreInit.class })
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public abstract class AbstractIntegrationTest {

	@Resource
	protected PersistenceService persistenceService;

	@Resource
	protected TransactionService transactionService;

	@Resource
	protected LoggingService loggingService;

	@Resource
	protected ModelService modelService;

	// protected AbstractIntegrationTest() {
	// final IntegrationTest testAnnotation =
	// ClassUtil.getAnnotation(this.getClass(), IntegrationTest.class);
	// Registry.setMainClass(testAnnotation.initClass());
	// }

	protected String getTestPackagePath() {
		return this.getClass().getPackage().getName();
	}

	/**
	 * Called before all tests are executed.
	 */
	@BeforeClass
	public static void initialize() {
	}

	/**
	 * Called when all tests have been executed.
	 */
	@AfterClass
	public static void shutdown() {
	}

	/**
	 * Called before each test is executed.
	 */
	@Before
	public void beforeTest() {
		MockitoAnnotations.initMocks(this);

		try {
			transactionService.start();
			prepareTest();
		} catch (final Exception e) {
			loggingService.exception(String.format("Could not prepare test %s", this.getClass().getName()), e);
		}
	}

	/**
	 * Called after each test has been executed.
	 */
	@After
	public void afterTest() {
		try {
			teardownTest();
			transactionService.rollback();
		} catch (final Exception e) {
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
