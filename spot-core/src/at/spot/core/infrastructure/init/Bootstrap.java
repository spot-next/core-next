package at.spot.core.infrastructure.init;

import java.util.Set;

import org.reflections.Reflections;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Bootstrap {
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	public static void main(final String[] args) throws Exception {
		// find all module init classes
		final Reflections reflections = new Reflections("");
		final Set<Class<? extends ModuleInit>> inits = reflections.getSubTypesOf(ModuleInit.class);

		AnnotationConfigApplicationContext ctx = null;

		try {
			// create a generic spring context
			ctx = new AnnotationConfigApplicationContext();

			// register all found module inits in that spring context
			for (final Class<? extends ModuleInit> init : inits) {
				init.newInstance().injectBeanDefinition(ctx);
			}

			ctx.registerShutdownHook();

			ctx.refresh();
			ctx.start();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			// MiscUtil.closeQuietly(ctx);
		}
	}

	/**
	 * Sets org.reflections logging to warnings, as we scan all package paths.
	 * This causes a lot of debug messages being logged.
	 */
	protected static void setLogSettings() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections", "warn");
	}
}
