package at.spot.instrumentation;

import static org.junit.Assert.assertTrue;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;

import at.spot.instrumentation.DynamicInstrumentationLoader;

@NotThreadSafe
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
