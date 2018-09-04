package io.spotnext.core.management.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.core.management.support.NoAuthenticationFilter;
import io.spotnext.core.management.transformer.PlainTextResponseTransformer;
import io.spotnext.core.management.transformer.ResponseTransformer;
import spark.route.HttpMethod;

/**
 * <p>Handler class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Target({ METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Handler {

	HttpMethod method() default HttpMethod.get;

	/**
	 * Defines the URL path that will be handled. It will be appended to the
	 * {@link RemoteEndpoint#pathMapping()}.
	 */
	String pathMapping() default "";

	/**
	 * This is the URL path that this handler method will handle. The path mapping
	 * defined on the containing class (via the {@link RemoteEndpoint} annotation)
	 * will be used as suffix.
	 * 
	 * @return the URL path that will be handled.
	 */
	MimeType mimeType() default MimeType.JAVASCRIPT;

	/**
	 * Defines the {@link ResponseTransformer} that will be used to convert the
	 * response in the desired format.
	 * 
	 * @return the response transformer implementation
	 */
	Class<? extends ResponseTransformer> responseTransformer() default PlainTextResponseTransformer.class;

	/**
	 * Defines the filter that is used authenticate incoming requests. By default
	 * the {@link NoAuthenticationFilter} is used, it accepts all requests.
	 *
	 * It overrides the {@link RemoteEndpoint#authenticationFilter()}
	 * 
	 * @return the defined authentication filter
	 */
	Class<? extends AuthenticationFilter> authenticationFilter() default NoAuthenticationFilter.class;

}
