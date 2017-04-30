package at.spot.spring.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.EventListener;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import at.spot.core.infrastructure.support.init.ModuleInit;

/**
 * This mixin serves as a start point for an embedded jetty server to boot a
 * {@link ModuleInit} class.
 */
public class EmbeddedJettyServer<I extends WebModuleInit> {

	protected int port;
	protected String contextPath;
	private I init;

	public EmbeddedJettyServer(I init, String contextPath, int port) {
		this.contextPath = contextPath;
		this.port = port;
		this.init = init;
	}

	public void start() throws Exception {
		final Server server = new Server(this.port);
		server.setHandler(getServletContextHandler(this.contextPath, init));

		server.start();
		server.join();
	}

	/**
	 * Set JSP to use Standard JavaC always.
	 * 
	 * @throws IOException
	 */
	protected void setupJspCompiler(WebAppContext context) throws IOException {
		System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
		context.setAttribute("javax.servlet.context.tempdir",
				Files.createTempDirectory("jetty").toAbsolutePath().toString());

		setupClassLoader(context);

		// context.addServlet(JspServlet.class, "*.jsp");

		ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
		holderJsp.setInitOrder(0);
		context.addServlet(holderJsp, "*.jsp");

		ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
		holderDefault.setInitParameter("resourceBase", URI.create(context.getResourceBase()).toASCIIString());
		holderDefault.setInitParameter("dirAllowed", "true");
		context.addServlet(holderDefault, "/");
	}

	protected void setupClassLoader(ContextHandler context) {
		ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
		context.setClassLoader(jspClassLoader);
		// context.setClassLoader(Thread.currentThread().getContextClassLoader());
	}

	protected ServletContextHandler getServletContextHandler(String contextPath, I init)
			throws IOException, InstantiationException, IllegalAccessException {

		final String webappDirLocation = new File("WebContent").getAbsolutePath();
		System.out.println("Configuring app with basedir: " + webappDirLocation);

		final WebAppContext webAppContext = new WebAppContext();
		webAppContext.setErrorHandler(null);
		webAppContext.setResourceBase(webappDirLocation);
		webAppContext.setContextPath(contextPath);
		webAppContext.addEventListener((EventListener) init);
		webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/target/classes/");
		webAppContext.getServletContext().setExtendedListenerTypes(true);

		// required for JSP servlet
		setupJspCompiler(webAppContext);

		return webAppContext;
	}
}
