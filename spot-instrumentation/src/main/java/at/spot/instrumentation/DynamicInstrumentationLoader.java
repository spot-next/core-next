package at.spot.instrumentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import at.spot.instrumentation.internal.AgentClassLoaderReference;
import at.spot.instrumentation.internal.DynamicInstrumentationAgent;
import at.spot.instrumentation.internal.DynamicInstrumentationLoadAgentMain;
import at.spot.instrumentation.internal.JdkFilesFinder;

/**
 * This class installs dynamic instrumentation into the current JVM.
 */
@ThreadSafe
public final class DynamicInstrumentationLoader {

	private static final String LOAD_AGENT_THREAD_NAME = "instrumentationAgentStarter";

	private static volatile Throwable threadFailed;
	private static volatile String toolsJarPath;
	private static volatile String attachLibPath;

	private static Class<? extends ClassFileTransformer>[] registeredTranformers;

	/**
	 * Keeping a reference here so it is not garbage collected
	 */
	static GenericXmlApplicationContext ltwCtx;

	protected DynamicInstrumentationLoader() {
	}

	public static void initialize(final Class<? extends ClassFileTransformer>... transformers) {
		registeredTranformers = transformers;

		try {
			while (!isInstrumentationAvailable() && threadFailed == null) {
				TimeUnit.MILLISECONDS.sleep(1);
			}
			if (threadFailed != null) {
				final String javaVersion = getJavaVersion();
				final String javaHome = getJavaHome();
				throw new RuntimeException("Additional information: javaVersion=" + javaVersion + "; javaHome="
						+ javaHome + "; toolsJarPath=" + toolsJarPath + "; attachLibPath=" + attachLibPath,
						threadFailed);
			}
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Checks if the instrumentation is enabled.
	 */
	public static boolean isInstrumentationAvailable() {
		return InstrumentationLoadTimeWeaver.isInstrumentationAvailable();
	}

	/**
	 * Creates a generic spring context with enabled load time weaving.
	 */
	public static synchronized GenericXmlApplicationContext initLoadTimeWeavingSpringContext() {
		org.assertj.core.api.Assertions.assertThat(isInstrumentationAvailable()).isTrue();

		if (ltwCtx == null) {
			final GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
			ctx.load(new ClassPathResource("/META-INF/ctx.spring.weaving.xml"));
			ctx.refresh();
			ltwCtx = ctx;
		}

		return ltwCtx;
	}

	static {
		if (!isInstrumentationAvailable()) {
			try {
				final File tempAgentJar = createTempAgentJar();
				setAgentClassLoaderReference();
				final String pid = DynamicInstrumentationProperties.getProcessId();
				final Thread loadAgentThread = new Thread(LOAD_AGENT_THREAD_NAME) {

					@Override
					public void run() {
						try {
							loadAgent(tempAgentJar, pid);
						} catch (final Throwable e) {
							threadFailed = e;
							throw new RuntimeException(e);
						}
					}
				};

				DynamicInstrumentationReflections.addPathToSystemClassLoader(tempAgentJar);

				final JdkFilesFinder jdkFilesFinder = new JdkFilesFinder();

				if (DynamicInstrumentationReflections.isBeforeJava9()) {
					final File toolsJar = jdkFilesFinder.findToolsJar();
					DynamicInstrumentationReflections.addPathToSystemClassLoader(toolsJar);
					DynamicInstrumentationLoader.toolsJarPath = toolsJar.getAbsolutePath();

					final File attachLib = jdkFilesFinder.findAttachLib();
					DynamicInstrumentationReflections.addPathToJavaLibraryPath(attachLib.getParentFile());
					DynamicInstrumentationLoader.attachLibPath = attachLib.getAbsolutePath();
				}

				loadAgentThread.start();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}

		}
	}

	protected static void loadAgent(final File tempAgentJar, final String pid) throws Exception {
		if (DynamicInstrumentationReflections.isBeforeJava9()) {
			DynamicInstrumentationLoadAgentMain.loadAgent(pid, tempAgentJar.getAbsolutePath());
		} else {
			// -Djdk.attach.allowAttachSelf
			// https://www.bountysource.com/issues/45231289-self-attach-fails-on-jdk9
			// workaround this limitation by attaching from a new process
			final File loadAgentJar = createTempJar(DynamicInstrumentationLoadAgentMain.class, false);
			final String javaExecutable = getJavaHome() + "/bin/java";
			final List<String> command = new ArrayList<String>();
			command.add(javaExecutable);
			command.add("-classpath");
			command.add(loadAgentJar.getAbsolutePath()); // tools.jar not needed
															// since java9
			command.add(DynamicInstrumentationLoadAgentMain.class.getName());
			command.add(pid);
			command.add(tempAgentJar.getAbsolutePath());

			new ProcessExecutor().command(command).destroyOnExit().exitValueNormal()
					.redirectOutput(Slf4jStream.of(DynamicInstrumentationLoader.class).asInfo())
					.redirectError(Slf4jStream.of(DynamicInstrumentationLoader.class).asWarn()).execute();
		}
	}

	protected static String getJavaHome() {
		// CHECKSTYLE:OFF
		return System.getProperty("java.home");
		// CHECKSTYLE:ON
	}

	protected static String getJavaVersion() {
		// CHECKSTYLE:OFF
		return System.getProperty("java.version");
		// CHECKSTYLE:ON
	}

	protected static void setAgentClassLoaderReference() throws Exception {
		final Class<AgentClassLoaderReference> agentClassLoaderReferenceClass = AgentClassLoaderReference.class;
		final File tempAgentClassLoaderJar = createTempJar(agentClassLoaderReferenceClass, false);
		DynamicInstrumentationReflections.addPathToSystemClassLoader(tempAgentClassLoaderJar);
		final ClassLoader systemClassLoader = DynamicInstrumentationReflections.getSystemClassLoader();
		final Class<?> systemAgentClassLoaderReferenceClass = systemClassLoader
				.loadClass(agentClassLoaderReferenceClass.getName());
		final Method setAgentClassLoaderMethod = systemAgentClassLoaderReferenceClass
				.getDeclaredMethod("setAgentClassLoader", ClassLoader.class);
		setAgentClassLoaderMethod.invoke(null, DynamicInstrumentationReflections.getContextClassLoader());
	}

	protected static File createTempAgentJar() throws ClassNotFoundException {
		try {
			return createTempJar(DynamicInstrumentationAgent.class, true);
		} catch (final Throwable e) {
			final String message = "Unable to find class [at.spot.instrumentation.internal.DynamicInstrumentationAgent] in classpath."
					+ "\nPlease make sure you have added invesdwin-instrument.jar to your classpath properly,"
					+ "\nor make sure you have embedded it correctly into your fat-jar."
					+ "\nThey can be created e.g. with \"maven-shade-plugin\"."
					+ "\nPlease be aware that some fat-jar solutions might not work well due to classloader issues.";
			throw new ClassNotFoundException(message, e);
		}
	}

	/**
	 * Creates a new jar that only contains the DynamicInstrumentationAgent
	 * class.
	 */
	protected static File createTempJar(final Class<?> clazz, final boolean agent) throws Exception {
		final String className = clazz.getName();
		final File tempAgentJar = new File(DynamicInstrumentationProperties.TEMP_DIRECTORY, className + ".jar");
		final Manifest manifest = new Manifest(clazz.getResourceAsStream("/META-INF/MANIFEST.MF"));
		if (agent) {
			manifest.getMainAttributes().putValue("Premain-Class", className);
			manifest.getMainAttributes().putValue("Agent-Class", className);
			manifest.getMainAttributes().putValue("Can-Redefine-Classes", String.valueOf(true));
			manifest.getMainAttributes().putValue("Can-Retransform-Classes", String.valueOf(true));
		}
		final JarOutputStream tempJarOut = new JarOutputStream(new FileOutputStream(tempAgentJar), manifest);
		final JarEntry entry = new JarEntry(className.replace(".", "/") + ".class");
		tempJarOut.putNextEntry(entry);
		final InputStream classIn = DynamicInstrumentationReflections.getClassInputStream(clazz);
		IOUtils.copy(classIn, tempJarOut);
		tempJarOut.closeEntry();
		tempJarOut.close();
		return tempAgentJar;
	}

}
