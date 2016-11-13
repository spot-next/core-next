package at.spot.core.infrastructure.init;

import java.util.Set;

import org.reflections.Reflections;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		// setLogSettings();

		// find all module init classes
		Reflections reflections = new Reflections("");
		Set<Class<? extends ModuleInit>> inits = reflections.getSubTypesOf(ModuleInit.class);

		AnnotationConfigApplicationContext ctx = null;

		try {
			// create a generic spring context
			ctx = new AnnotationConfigApplicationContext();

			// register all found module inits in that spring context
			for (Class<? extends ModuleInit> init : inits) {
				init.newInstance().injectBeanDefinition(ctx);
			}

			ctx.registerShutdownHook();

			ctx.refresh();
			ctx.start();
		} catch (Exception e) {
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

	/**
	 * Sets up gson convertors
	 */
	protected static void setupGson() {
		final Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
	}
}
