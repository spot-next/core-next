package io.spotnext.core.testing;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.TestPropertySource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.persistence.service.TransactionService;

/**
 * This is the base class for all integration tasks. Database access will be
 * reverted after the each test using a transaction rollback. Any initialized
 * {@link io.spotnext.infrastructure.support.init.ModuleInit}s must be set
 * using the
 * {@link org.springframework.boot.test.context.SpringBootTest#classes()}
 * annotation. By default {@link io.spotnext.core.CoreInit} is defined. The main
 * {@link io.spotnext.infrastructure.support.init.ModuleInit} has to be
 * defined to using
 * {@link io.spotnext.core.testing.IntegrationTest#initClass()}, if the test
 * depends on it.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@TestPropertySource(locations = "classpath:/core-testing.properties")
@RunWith(SpotJunitRunner.class)
@IntegrationTest
@SpringBootTest(classes = CoreInit.class)
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public abstract class AbstractIntegrationTest implements ApplicationContextAware {

	private static final int waitInMillis = 500;

	private int maxMillisToWaitForModuleInitialization = 10 * 60 * 60 * 1000;
	private ApplicationContext applicationContext;

	@Resource
	protected PersistenceService persistenceService;

	@Resource
	protected TransactionService transactionService;

	@Resource
	protected LoggingService loggingService;

	@Resource
	protected ModelService modelService;

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
	 *
	 * @throws InterruptedException if the thead wait was interrupted
	 * @throw IllegalStateException if module initialization didn't finish within
	 *        the max allowed time.
	 */
	@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "injected by spring")
	@Before
	public void beforeTest() throws InterruptedException {
		MockitoAnnotations.initMocks(this);

		final Class<? extends ModuleInit> initClass = Registry.getMainClass();
		final ModuleInit initModule = applicationContext.getBean(initClass);

		int waitedInMillis = 0;

		// TODO: use application ready event instead of waiting?
		while (!initModule.isAlreadyInitialized()) {
			Thread.sleep(waitInMillis);
			waitedInMillis += waitInMillis;

			if (waitedInMillis >= maxMillisToWaitForModuleInitialization) {
				throw new IllegalStateException(
						String.format("ModuleInit did not finish initialization within %s seconds",
								maxMillisToWaitForModuleInitialization / 1000));
			}
		}

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

	/** {@inheritDoc} */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * <p>
	 * Getter for the field <code>maxMillisToWaitForModuleInitialization</code>.
	 * </p>
	 *
	 * @return a int.
	 */
	public int getMaxMillisToWaitForModuleInitialization() {
		return maxMillisToWaitForModuleInitialization;
	}

	/**
	 * <p>
	 * Setter for the field <code>maxMillisToWaitForModuleInitialization</code>.
	 * </p>
	 *
	 * @param maxMillisToWaitForModuleInitialization a int.
	 */
	public void setMaxMillisToWaitForModuleInitialization(final int maxMillisToWaitForModuleInitialization) {
		this.maxMillisToWaitForModuleInitialization = maxMillisToWaitForModuleInitialization;
	}

	/**
	 * Calls {@link Thread#wait(long)} but catches and ignores all errors.
	 */
	protected void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
