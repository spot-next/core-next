package at.spot.spring.web.jetty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.EventListener;
import java.util.logging.Logger;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.spring.web.WebModuleInit;
import at.spot.spring.web.exception.ServerStartupException;
import at.spot.spring.web.jetty.jsp.JspStarter;

/**
 * This mixin serves as a start point for an embedded jetty server to boot a
 * {@link ModuleInit} class.
 */
public class EmbeddedJettyServer<I extends WebModuleInit> {
	private static Logger LOG = Logger.getLogger(EmbeddedJettyServer.class.getName());

	// Resource path pointing to where the WEBROOT is
	protected String webappRootDir;
	protected int port;
	protected String contextPath;
	protected I init;

	public EmbeddedJettyServer(I init, String webappRootDir, String contextPath, int port) {
		this.webappRootDir = webappRootDir;
		this.contextPath = contextPath;
		this.port = port;
		this.init = init;
	}

	public void start() throws ServerStartupException {
		final Server server = new Server(this.port);

		try {
			server.setHandler(getServletContextHandler(this.contextPath, init));
			server.start();
			server.join();
		} catch (Exception e) {
			throw new ServerStartupException(
					String.format("Could not start embedded jetty server on port %s", this.port), e);
		}
	}

	protected ServletContextHandler getServletContextHandler(String contextPath, I init)
			throws IOException, InstantiationException, IllegalAccessException, URISyntaxException {

		URI webAppResourceBaseDir = getWebRootResourceUri();

		System.out.println("Configuring app with basedir: " + webAppResourceBaseDir);

		final WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath(contextPath);
		webAppContext.setErrorHandler(null);
		webAppContext.setResourceBase(".");
		webAppContext.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
		webAppContext.addEventListener((EventListener) init);
		webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/target/classes/");
		webAppContext.getServletContext().setExtendedListenerTypes(true);

		// JSP support
		webAppContext.setAttribute("javax.servlet.context.tempdir",
				Files.createTempDirectory("jetty").toAbsolutePath().toString());

		webAppContext.addBean(new JspStarter(webAppContext));
		webAppContext.setClassLoader(getUrlClassLoader(webAppContext));

		webAppContext.addServlet(getJspServletHolder(), "*.jsp");
		// default servlet is necessary for JSP support
		webAppContext.addServlet(getDefaultServletHolder(webAppResourceBaseDir), "/");

		return webAppContext;
	}

	protected ClassLoader getUrlClassLoader(ContextHandler context) {
		ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
		return jspClassLoader;
	}

	/**
	 * Create JSP Servlet (must be named "jsp")
	 */
	private ServletHolder getJspServletHolder() {
		ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
		holderJsp.setInitOrder(0);
		holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
		holderJsp.setInitParameter("fork", "false");
		holderJsp.setInitParameter("xpoweredBy", "false");
		holderJsp.setInitParameter("compilerTargetVM", "1.7");
		holderJsp.setInitParameter("compilerSourceVM", "1.7");
		holderJsp.setInitParameter("keepgenerated", "true");
		return holderJsp;
	}

	/**
	 * Create Default Servlet (must be named "default")
	 */
	private ServletHolder getDefaultServletHolder(URI baseUri) {
		ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
		LOG.info("Base URI: " + baseUri);
		holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
		holderDefault.setInitParameter("dirAllowed", "true");
		return holderDefault;
	}

	private URI getWebRootResourceUri() throws FileNotFoundException, URISyntaxException {
		final File webappDirLocation = new File(webappRootDir);
		URI indexUri = webappDirLocation.toURI();

		if (indexUri == null) {
			throw new FileNotFoundException("Unable to find resource " + webappRootDir);
		}

		// Points to wherever /webroot/ (the resource) is
		return indexUri;
	}

}
