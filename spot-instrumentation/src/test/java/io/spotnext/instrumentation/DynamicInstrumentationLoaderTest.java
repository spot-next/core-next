package io.spotnext.instrumentation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;

public class DynamicInstrumentationLoaderTest {

	static {
		// dynamically attach java agent to jvm if not already present
		DynamicInstrumentationLoader.initialize();
	}

	@Test
	public void test() {
		assertTrue(InstrumentationLoadTimeWeaver.isInstrumentationAvailable());
	}
}
