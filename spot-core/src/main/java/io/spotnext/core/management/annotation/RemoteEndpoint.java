package io.spotnext.core.management.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Service
public @interface RemoteEndpoint {
	/**
	 * The port the endpoint will be using. Multiple endpoints can share the
	 * same port
	 * 
	 * @return the port this endpoint is running on.
	 */
	int port() default 8080;

	/**
	 * @return the property key that holds the port. Overrides the
	 *         {@link #port()} property. If it is empty or null the default will
	 *         be used.
	 */
	String portConfigKey() default "";

	/**
	 * This is the base URL path that this endpoint will handle. All path
	 * mapping defined on its handler methods (annotated with {@link Handler})
	 * will use this as a suffix.
	 * 
	 * @return the URL path that will be handled.
	 */
	String pathMapping() default "";

}
