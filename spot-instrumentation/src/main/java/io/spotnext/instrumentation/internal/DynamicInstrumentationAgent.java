package io.spotnext.instrumentation.internal;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Immutable
/**
 * <p>
 * DynamicInstrumentationAgent class.
 * </p>
 *
 * @since 1.0
 */
public final class DynamicInstrumentationAgent {

	private static final Logger LOG = LoggerFactory.getLogger(DynamicInstrumentationAgent.class);

	private DynamicInstrumentationAgent() {
	}

	/**
	 * <p>
	 * premain.
	 * </p>
	 *
	 * @param args a {@link java.lang.String} object.
	 * @param inst a {@link java.lang.instrument.Instrumentation} object.
	 * @throws java.lang.Exception if any.
	 */
	public static void premain(final String args, final Instrumentation inst)
			throws Exception {

		try {
			final ClassLoader agentClassLoader = AgentClassLoaderReference
					.getAgentClassLoader();
			final Class<?> agentInstrumentationInitializer = agentClassLoader
					.loadClass(DynamicInstrumentationAgent.class.getPackage().getName() + ".AgentInstrumentationInitializer");
			final Method initializeMethod = agentInstrumentationInitializer.getDeclaredMethod("initialize", String.class,
					Instrumentation.class);

			String transformers = System.getProperty("transformers");

			if (StringUtils.isNotBlank(transformers)) {
				LOG.debug("Registering class transformers: " + transformers);

				loadClassTransformers(transformers, inst);
			}
			initializeMethod.invoke(null, args, inst);
		} catch (Exception e) {
			LOG.error("Could not initialize instrumentation", e);
		}
	}

	/**
	 * <p>
	 * agentmain.
	 * </p>
	 *
	 * @param args a {@link java.lang.String} object.
	 * @param inst a {@link java.lang.instrument.Instrumentation} object.
	 * @throws java.lang.Exception if any.
	 */
	public static void agentmain(final String args, final Instrumentation inst)
			throws Exception {

		premain(args, inst);
	}

	/**
	 * Parses the comma-separated list of transformers and instantiate them. Then they are added to the instrumentation.
	 * 
	 * @param transformersProperty the command separated list of transformers
	 * @param instrumentation      instance
	 * @throws Exception
	 */
	protected static void loadClassTransformers(String transformersProperty, Instrumentation instrumentation) throws Exception {
		if (transformersProperty != null) {
			for (String t : transformersProperty.split(",")) {
				t = t.trim();
				final ClassFileTransformer trans = (ClassFileTransformer) Class.forName(t).getDeclaredConstructor().newInstance();
				instrumentation.addTransformer(trans);
			}
		}
	}
}
