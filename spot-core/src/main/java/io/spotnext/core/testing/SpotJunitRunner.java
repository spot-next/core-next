package io.spotnext.core.testing;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>SpotJunitRunner class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SpotJunitRunner extends SpringJUnit4ClassRunner {

	protected IntegrationTest testAnnotation;

	/**
	 * <p>Constructor for SpotJunitRunner.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 * @throws org.junit.runners.model.InitializationError if any.
	 */
	public SpotJunitRunner(final Class<?> clazz) throws InitializationError {
		super(clazz);

		testAnnotation = ClassUtil.getAnnotation(clazz, IntegrationTest.class);

		Registry.setMainClass(this.testAnnotation.initClass());
	}

	/** {@inheritDoc} */
	@Override
	public void run(final RunNotifier notifier) {
		notifier.addListener(new SpotJunitRunListener());
		notifier.fireTestRunStarted(getDescription());
		super.run(notifier);
	}
}
