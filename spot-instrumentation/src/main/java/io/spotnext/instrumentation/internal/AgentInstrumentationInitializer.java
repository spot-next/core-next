package io.spotnext.instrumentation.internal;

import java.lang.instrument.Instrumentation;

import org.springframework.instrument.InstrumentationSavingAgent;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;

/**
 * <p>AgentInstrumentationInitializer class.</p>
 *
 * @since 1.0
 */
public final class AgentInstrumentationInitializer {

	private AgentInstrumentationInitializer() {
	}

	/**
	 * <p>initialize.</p>
	 *
	 * @param args a {@link java.lang.String} object.
	 * @param inst a {@link java.lang.instrument.Instrumentation} object.
	 */
	public static void initialize(final String args, final Instrumentation inst) {
		if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
			throw new IllegalStateException(
					"Instrumentation is already available, the agent should have been loaded already!");
		}

		InstrumentationSavingAgent.premain(args, inst);
	}

}
