package io.spotnext.core.management.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.transformer.PlainTextResponseTransformer;
import spark.ResponseTransformer;
import spark.route.HttpMethod;

@Target({ METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

	HttpMethod method() default HttpMethod.get;

	/**
	 * Defines the URL path that will be handled.
	 */
	String pathMapping() default "";

	/**
	 * This is the URL path that this handler method will handle. The path
	 * mapping defined on the containing class (via the {@link RemoteEndpoint}
	 * annotation) will be used as suffix.
	 * 
	 * @return the URL path that will be handled.
	 */
	MimeType mimeType() default MimeType.JAVASCRIPT;

	/**
	 * Defines the {@link ResponseTransformer} that will be used to convert the
	 * response in the desired format.
	 */
	Class<? extends ResponseTransformer> responseTransformer() default PlainTextResponseTransformer.class;
}
