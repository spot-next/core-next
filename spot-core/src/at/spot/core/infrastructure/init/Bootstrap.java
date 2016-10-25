package at.spot.core.infrastructure.init;

import java.util.Set;

import org.reflections.Reflections;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import at.spot.core.support.util.MiscUtil;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		// find all module init classes
		Reflections reflections = new Reflections();
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
			MiscUtil.closeQuietly(ctx);
		}
	}
}
