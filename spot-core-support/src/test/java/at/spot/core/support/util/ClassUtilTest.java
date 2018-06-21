package at.spot.core.support.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import at.spot.core.support.util.data.TestClass;

public class ClassUtilTest {

	@Test
	public void testInstantiationOfStaticInnerClass() {
		Optional<StaticInnerClass> instance = ClassUtil.instantiate(StaticInnerClass.class);

		Assert.assertTrue(instance.isPresent());
	}

	@Test
	public void testInstantiationOfInnerClass() {
		Optional<InnerClass> instance = ClassUtil.instantiate(InnerClass.class);

		Assert.assertTrue(instance.isPresent());
	}

	@Test
	public void testInstantiationOfClass() {
		Optional<TestClass> instance = ClassUtil.instantiate(TestClass.class);

		Assert.assertTrue(instance.isPresent());
	}

	private static class StaticInnerClass {
		//
	}

	private class InnerClass {
		//
	}
}
