package at.spot.core.testing;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.support.util.ClassUtil;

public class SpotJunitRunListener extends RunListener {

	protected IntegrationTest testAnnotation;

	@Override
	public void testRunStarted(final Description description) throws Exception {
		if (description.getTestClass() != null) {
			testAnnotation = ClassUtil.getAnnotation(description.getTestClass(), IntegrationTest.class);

			Registry.setMainClass(this.testAnnotation.initClass());
		}
	}

}
