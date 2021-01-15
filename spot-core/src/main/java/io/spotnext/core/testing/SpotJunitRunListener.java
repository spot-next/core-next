package io.spotnext.core.testing;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>
 * SpotJunitRunListener class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SpotJunitRunListener implements TestExecutionListener {

	static {
		ModuleInit.initializeWeavingSupport();
	}

	protected IntegrationTest testAnnotation;

//	/** {@inheritDoc} */
//	@Override
//	public void testRunStarted(final Description description) throws Exception {
//		if (description != null && description.getTestClass() != null) {
//			testAnnotation = ClassUtil.getAnnotation(description.getTestClass(), IntegrationTest.class);
//
//			if (testAnnotation != null) {
//				Registry.setMainClass(this.testAnnotation.initClass());
//			}
//		}
//	}

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		if (testContext.getTestClass() != null) {
			testAnnotation = ClassUtil.getAnnotation(testContext.getTestClass(), IntegrationTest.class);

			if (testAnnotation != null) {
				Registry.setMainClass(this.testAnnotation.initClass());
			}
		}
		
		TestExecutionListener.super.beforeTestClass(testContext);
	}

}
