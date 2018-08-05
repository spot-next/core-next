package io.spotnext.instrumentation.internal;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.spotnext.instrumentation.ClassTransformer;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;

// @Immutable
public final class DynamicInstrumentationAgent {

	private DynamicInstrumentationAgent() {
	}

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

		for (final ClassFileTransformer t : findClassTransformers()) {
			inst.addTransformer(t);
		}

		initializeMethod.invoke(null, args, inst);
	}

	public static void agentmain(final String args, final Instrumentation inst)
			throws Exception {

		premain(args, inst);
	}

	protected static <T extends ClassFileTransformer> List<T> findClassTransformers() {
		final List<T> transformers = new ArrayList<>();

		new FastClasspathScanner().matchClassesImplementing(
				ClassFileTransformer.class,
				new ImplementingClassMatchProcessor<ClassFileTransformer>() {
					@Override
					public void processMatch(
							final Class<? extends ClassFileTransformer> implementingClass) {
						if (implementingClass
								.isAnnotationPresent(ClassTransformer.class)) {
							try {
								final T transformer = (T) implementingClass
										.newInstance();

								transformers.add(transformer);
							} catch (final Exception e) {
								throw new RuntimeException(String.format(
										"Could not instantiate ClassFileTransformer '%s'",
										implementingClass.getName()), e);
							}
						}
					}
				}).scan();

		return transformers;
	}
}
