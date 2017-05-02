package at.spot.spring.web.jetty.jsp;

import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

/**
 * JspStarter
 * 
 * This is added as a bean that is a Jetty LifeCycle on the
 * ServletContextHandler. This bean's doStart method will be called as the
 * ServletContextHandler starts, and will call the ServletContainerInitializer
 * for the JSP engine.
 */
public class JspStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller {
	JettyJasperInitializer sci;
	ServletContextHandler context;

	public JspStarter(ServletContextHandler context) {
		this.sci = new JettyJasperInitializer();
		this.context = context;
		this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
	}

	@Override
	protected void doStart() throws Exception {
		ClassLoader old = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(context.getClassLoader());
		try {
			sci.onStartup(null, context.getServletContext());
			super.doStart();
		} finally {
			Thread.currentThread().setContextClassLoader(old);
		}
	}
}