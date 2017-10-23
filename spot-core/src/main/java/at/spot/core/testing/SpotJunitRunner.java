package at.spot.core.testing;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.support.util.ClassUtil;
import de.invesdwin.instrument.DynamicInstrumentationLoader;

public class SpotJunitRunner extends SpringJUnit4ClassRunner {

	static {
		DynamicInstrumentationLoader.initialize();
	}

	protected IntegrationTest testAnnotation;

	public SpotJunitRunner(final Class<?> clazz) throws InitializationError {
		super(clazz);

		testAnnotation = ClassUtil.getAnnotation(clazz, IntegrationTest.class);

		Registry.setMainClass(this.testAnnotation.initClass());
	}
}
