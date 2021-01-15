package io.spotnext.core.testing;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>SpotJunitRunner class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SpotSpringExtension extends SpringExtension {

	static {
		ModuleInit.initializeWeavingSupport();
	}

	protected IntegrationTest testAnnotation;

	/**
	 * <p>Constructor for SpotJunitRunner.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 * @throws org.junit.runners.model.InitializationError if any.
	 */
	public SpotSpringExtension() {
		super();

	}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		testAnnotation = ClassUtil.getAnnotation(context.getTestClass().get(), IntegrationTest.class);

		if (testAnnotation != null) {
			Registry.setMainClass(this.testAnnotation.initClass());
		}
		
		super.beforeTestExecution(context);
	}
}
