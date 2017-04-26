package at.spot.spring.web;

import java.io.File;
import java.io.IOException;
import java.util.EventListener;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.springframework.web.WebApplicationInitializer;

import at.spot.core.infrastructure.support.init.ModuleInit;

/**
 * This mixin serves as a start point for an embedded jetty server to boot a
 * {@link ModuleInit} class.
 */
public interface EmbeddedJettyServer {
	default ServletContextHandler getServletContextHandler(String contextPath, Class<? extends WebModuleInit> initClass)
			throws IOException, InstantiationException, IllegalAccessException {
		final String webappDirLocation = new File("WebContent").getAbsolutePath();
		System.out.println("configuring app with basedir: " + webappDirLocation);

		final WebAppContext contex = new WebAppContext();
		contex.setErrorHandler(null);
		contex.setResourceBase(webappDirLocation);
		contex.setContextPath(contextPath);
		contex.addEventListener((EventListener) this);
		contex.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/target/classes/");

		contex.setConfigurations(new Configuration[] { new WebXmlConfiguration(), new AnnotationConfiguration() {
			@Override
			public void preConfigure(WebAppContext context) throws Exception {
				MultiMap<String> map = new MultiMap<String>();
				map.add(WebApplicationInitializer.class.getName(), initClass.getName());
				context.setAttribute(CLASS_INHERITANCE_MAP, map);
				_classInheritanceHandler = new ClassInheritanceHandler(map);
			}
		} });

		// required for JSP servlet
		contex.setClassLoader(Thread.currentThread().getContextClassLoader());
		contex.addServlet(JspServlet.class, "*.jsp");

		return contex;
	}

	default void start(String contextPath, int port, Class<? extends WebModuleInit> initClass) throws Exception {
		final Server server = new Server(port);
		server.setHandler(getServletContextHandler(contextPath, initClass));

		server.start();
		server.join();
	}
}
