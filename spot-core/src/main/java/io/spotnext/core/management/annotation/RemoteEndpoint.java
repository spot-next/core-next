package io.spotnext.core.management.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.core.management.support.NoAuthenticationFilter;
import io.spotnext.core.management.support.NoopAuthenticationFilter;

/**
 * <p>RemoteEndpoint class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
@Service
public @interface RemoteEndpoint {
	// if changed, also change the default value for authenticationFitler here and in the Handler annotation!
	public static final int DEFAULT_PORT = 8080;

	/**
	 * The port the endpoint will be using. Multiple endpoints can share the same
	 * port
	 * 
	 * @return the port this endpoint is running on.
	 */
	int port() default DEFAULT_PORT;

	/**
	 * @return the property key that holds the port. Overrides the {@link #port()}
	 *         property. If it is empty or null the default will be used.
	 */
	String portConfigKey() default "";

	/**
	 * This is the base URL path that this endpoint will handle. All path mapping
	 * defined on its handler methods (annotated with {@link Handler}) will use this
	 * as a suffix.
	 * 
	 * @return the URL path that will be handled.
	 */
	String[] pathMapping() default "";

	/**
	 * Defines the filter that is used authenticate incoming requests. By default
	 * the {@link NoAuthenticationFilter} is used, it accepts all requests.
	 * 
	 * @return the defined authentication filter
	 */
	Class<? extends AuthenticationFilter> authenticationFilter() default NoAuthenticationFilter.class;
}
