package at.spot.spring.web;

import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import at.spot.core.infrastructure.support.init.Bootstrap;
import at.spot.core.infrastructure.support.init.BootstrapOptions;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.spring.web.session.WebSessionListener;

/**
 * This interface extends the {@link ModuleInit} with some more functionality
 * with web container support.
 */
public interface WebModuleInit extends ServletContextListener, WebApplicationInitializer, ServletContainerInitializer {

	@Override
	default void contextInitialized(final ServletContextEvent event) {
	}

	@Override
	default void contextDestroyed(final ServletContextEvent event) {
	}

	/*
	 * *************************************************************************
	 * Embedded jetty initialization
	 * *************************************************************************
	 */

	/*
	 * *************************************************************************
	 * Tomcat initialization
	 * *************************************************************************
	 */

	/**
	 * This is the entry point when using an embedded jetty.
	 */
	@Override
	default void onStartup(Set<Class<?>> params, ServletContext servletContext) throws ServletException {
		onStartup(servletContext);
	}

	/**
	 * This is the spring entry point when using an servlet container like
	 * tomcat.
	 */
	@Override
	default void onStartup(final ServletContext servletContext) throws ServletException {
		startup(servletContext);
	}

	/**
	 * The spot core initialization process starts here. After if is finished,
	 * the web module is initialized.
	 * 
	 * @param servletContext
	 */
	default void startup(final ServletContext servletContext) {
		bootSpotCore(getModuleInitClass(), getApplicationConfigProperties(), null);
		loadWebModule(servletContext);
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
	 * This sets up the listeners, filters and main servlet.
	 * 
	 * @param servletContext
	 */
	default void loadWebModule(final ServletContext servletContext) {
		final WebApplicationContext context = getApplicationContext(servletContext);

		setupListeners(servletContext, context);
		setupFilters(servletContext, context);
		setupServlets(servletContext, context);
	}

	/**
	 * Setup the servlets - most likely just spring's DispatcherServlet
	 * 
	 * @param servletContext
	 * @param context
	 */
	default void setupServlets(final ServletContext servletContext, final WebApplicationContext context) {
		final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet",
				new DispatcherServlet(context));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}

	/**
	 * Set spring security filter mapping.
	 * 
	 * @param servletContext
	 * @param context
	 */
	default void setupFilters(final ServletContext servletContext, final ApplicationContext context) {
		final FilterRegistration.Dynamic filter = servletContext.addFilter("springSecurityFilterChain",
				DelegatingFilterProxy.class);

		filter.addMappingForUrlPatterns(null, false, "/*");
	}

	/**
	 * Registers {@link ServletContextListener}s. By default the
	 * {@link WebModuleInit} class is registered as listener too. Although the
	 * {@link WebModuleInit#contextInitialized(ServletContextEvent)} and
	 * {@link WebModuleInit#contextDestroyed(ServletContextEvent)} by default
	 * don't do anything.
	 * 
	 * @param servletContext
	 */
	default void setupListeners(final ServletContext servletContext, final WebApplicationContext context) {
		servletContext.addListener(this);

		servletContext.addListener(new ContextLoaderListener(context));
		// register a session listener that connects the web session to the spot
		// session service
		servletContext.addListener(WebSessionListener.class);
	}

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
	 * Returns the the {@link ModuleInit} class for this application.
	 * 
	 * @return
	 */
	<T extends ModuleInit> Class<T> getModuleInitClass();

	/**
	 * Returns the web spring context.
	 * 
	 * @param servletContext
	 * @return
	 */
	WebApplicationContext getApplicationContext(final ServletContext servletContext);

	/**
	 * Returns the main properties file.
	 * 
	 * @return
	 */
	String getApplicationConfigProperties();

}
