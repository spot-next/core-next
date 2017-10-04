package at.spot.core.testing;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.support.util.ClassUtil;

public class SpotJunitRunner extends SpringJUnit4ClassRunner {
	protected IntegrationTest testAnnotation;

	public SpotJunitRunner(Class<?> clazz) throws InitializationError {
		super(clazz);

		testAnnotation = ClassUtil.getAnnotation(clazz, IntegrationTest.class);

		Registry.setMainClass(this.testAnnotation.initClass());
	}
}
