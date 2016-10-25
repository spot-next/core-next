package at.spot.core.infrastructure.init;

import java.util.Set;

import org.reflections.Reflections;
import org.springframework.context.support.GenericApplicationContext;

import at.spot.core.infrastructure.service.LoggingService;

public class Bootstrap {
	public static void main(String[] args) throws Exception {

		// find all module init classes
		Reflections reflections = new Reflections();
		Set<Class<? extends ModuleInit>> inits = reflections.getSubTypesOf(ModuleInit.class);

		// create a generic spring context
		GenericApplicationContext ctx = new GenericApplicationContext();

		// register all found module inits in that spring context
		for (Class<? extends ModuleInit> init : inits) {
			init.newInstance().injectBeanDefinition(ctx);
		}

		ctx.refresh();

		LoggingService loggingService = ctx.getBean("loggingService", LoggingService.class);
		loggingService.info("Server start finished.");
	}
}
