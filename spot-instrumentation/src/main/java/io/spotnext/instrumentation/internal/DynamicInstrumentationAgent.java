package io.spotnext.instrumentation.internal;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

		try {
			final ClassLoader agentClassLoader = AgentClassLoaderReference.getAgentClassLoader();
			final Class<?> agentInstrumentationInitializer = agentClassLoader
					.loadClass(DynamicInstrumentationAgent.class.getPackage().getName() + ".AgentInstrumentationInitializer");
			final Method initializeMethod = agentInstrumentationInitializer.getDeclaredMethod("initialize", String.class,
					Instrumentation.class);

			String transformers = System.getProperty("transformers");

			// if null we try to parse the string args (java 9+)
			if (transformers == null) {
				transformers = parseArguments(args).get("transformers");
			}

			if (transformers != null && transformers.trim().length() > 0) {
				loadClassTransformers(transformers, agentClassLoader, inst);
			}
			initializeMethod.invoke(null, args, inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses the command line args into a map of argument-value pairs. The argument string has too look like this: key=value,key=value
	 * 
	 * @param args the command line argument string, can be null.
	 * @return the map of key-value pairs, can be empty but never null.
	 */
	protected static Map<String, String> parseArguments(String args) {
		final Map<String, String> parsedArgs = new HashMap<>();

		if (args != null) {
			String[] splitArgs = args.trim().split(",");

			for (String s : splitArgs) {
				if (s != null && s.trim().length() > 0) {
					String[] i = s.trim().split("=");

					if (i.length > 0) {
						parsedArgs.put(i[0], i.length > 1 ? i[1] : null);
					}
				}
			}
		}

		return parsedArgs;
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
	protected static void loadClassTransformers(String transformersProperty, ClassLoader classLoader, Instrumentation instrumentation) throws Exception {
		if (transformersProperty != null) {
			for (String t : transformersProperty.split(",")) {
				t = t.trim();
				final ClassFileTransformer trans = (ClassFileTransformer) classLoader.loadClass(t).getDeclaredConstructor().newInstance();
				instrumentation.addTransformer(trans);
			}
		}
	}
}
