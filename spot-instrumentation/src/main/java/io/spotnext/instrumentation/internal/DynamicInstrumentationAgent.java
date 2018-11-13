package io.spotnext.instrumentation.internal;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.spotnext.instrumentation.DynamicInstrumentationLoader;

// @Immutable
/**
 * <p>
 * DynamicInstrumentationAgent class.
 * </p>
 *
 * @since 1.0
 */
public final class DynamicInstrumentationAgent {

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

		final ClassLoader agentClassLoader = AgentClassLoaderReference
				.getAgentClassLoader();
		final Class<?> agentInstrumentationInitializer = agentClassLoader
				.loadClass(
						DynamicInstrumentationAgent.class.getPackage().getName()
								+ ".AgentInstrumentationInitializer");
		final Method initializeMethod = agentInstrumentationInitializer
				.getDeclaredMethod("initialize", String.class,
						Instrumentation.class);

		for (final ClassFileTransformer t : loadClassTransformers()) {
			inst.addTransformer(t);
		}

		initializeMethod.invoke(null, args, inst);
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
	 * Instantiate the {@link ClassFileTransformer}s registered in {@link DynamicInstrumentationLoader#getRegisteredTranformers()}.
	 *
	 * @param <T> a T object.
	 * @return a {@link java.util.List} object.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected static <T extends ClassFileTransformer> List<T> loadClassTransformers() throws InstantiationException, IllegalAccessException {
		final List<T> transformers = new ArrayList<>();

		for (Class<? extends ClassFileTransformer> t : DynamicInstrumentationLoader.getRegisteredTranformers()) {
			transformers.add((T) t.newInstance());
		}

		return transformers;
	}
}
