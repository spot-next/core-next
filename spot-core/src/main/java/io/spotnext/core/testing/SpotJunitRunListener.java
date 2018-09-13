package io.spotnext.core.testing;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>SpotJunitRunListener class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SpotJunitRunListener extends RunListener {

	protected IntegrationTest testAnnotation;

	/** {@inheritDoc} */
	@Override
	public void testRunStarted(final Description description) throws Exception {
		if (description != null && description.getTestClass() != null) {
			testAnnotation = ClassUtil.getAnnotation(description.getTestClass(), IntegrationTest.class);

			Registry.setMainClass(this.testAnnotation.initClass());
		}
	}

}
