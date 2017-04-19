package at.spot.spring.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

import at.spot.core.infrastructure.support.init.Bootstrap;
import at.spot.core.infrastructure.support.init.BootstrapOptions;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.spring.web.session.WebSessionListener;

/**
 * This interface extends the {@link ModuleInit} with some more functionality
 * with web container support.
 */
public interface WebModuleInit extends WebApplicationInitializer, ServletContextListener {
	@Override
	default void onStartup(final ServletContext servletContext) throws ServletException {
		bootSpotCore(getModuleInitClass(), getApplicationConfigProperties(), null);
		loadWebModule(servletContext);
	}

	default void loadWebModule(final ServletContext servletContext) {
		final WebApplicationContext context = getApplicationContext(servletContext);

		setupServlets(servletContext, context);
		setupFilters(servletContext, context);
		setupListeners(servletContext);
	}

	/**
	 * Registers {@link ServletContextListener}s. By default the
	 * {@link WebModuleInit} class is registered as listener too. Althrought the
	 * {@link WebModuleInit#contextInitialized(ServletContextEvent)} and
	 * {@link WebModuleInit#contextDestroyed(ServletContextEvent)} by default
	 * don't do anything.
	 * 
	 * @param servletContext
	 */
	default void setupListeners(final ServletContext servletContext) {
		servletContext.addListener(this);
		// register a session listener that connects the web session to the spot
		// session service
		servletContext.addListener(WebSessionListener.class);
	}

	/**
	 * Startup the spot core bootstrap mechanism. Registers {@link ModuleInit}
	 * as the spot module init class.<br />
	 * Also allows to inject app properties and spring configuration.
	 * 
	 * @param servletContext
	 */
	default <T extends ModuleInit> void bootSpotCore(final Class<T> initClass, final String appConfigFile,
			final String springConfigFile) {

		final BootstrapOptions conf = new BootstrapOptions();

		if (initClass != null) {
			conf.setInitClass(initClass);
		}

		if (StringUtils.isNotEmpty(appConfigFile)) {
			conf.setAppConfigFile(appConfigFile);
		}

		if (StringUtils.isNotEmpty(springConfigFile)) {
			conf.setSpringConfigFile(springConfigFile);
		}

		Bootstrap.bootstrap(conf);
	}

	/**
	 * Returns the web spring context.
	 * 
	 * @param servletContext
	 * @return
	 */
	WebApplicationContext getApplicationContext(final ServletContext servletContext);

	String getApplicationConfigProperties();

	/**
	 * Returns the the {@link ModuleInit} class for this application.
	 * 
	 * @return
	 */
	<T extends ModuleInit> Class<T> getModuleInitClass();

	/**
	 * Returns the spot base spring context, registered in
	 * {@link Registry#getApplicationContext()}.
	 * 
	 * @return
	 */
	default ApplicationContext getParentSpringContext() {
		return Registry.getApplicationContext();
	}

	/**
	 * Setup the servlets - most likely just spring's DispatcherServlet
	 * 
	 * @param servletContext
	 * @param context
	 */
	void setupServlets(final ServletContext servletContext, final WebApplicationContext context);

	/**
	 * 
	 * @param servletContext
	 * @param context
	 */
	void setupFilters(final ServletContext servletContext, final ApplicationContext context);

	@Override
	default void contextInitialized(final ServletContextEvent event) {
	}

	@Override
	default void contextDestroyed(final ServletContextEvent event) {
	}
}
