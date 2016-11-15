package at.spot.core.management.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.management.transformer.PlainTExtResponseTransformer;
import spark.ResponseTransformer;
import spark.route.HttpMethod;

@Target({ METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

	HttpMethod method() default HttpMethod.get;

	/**
	 * Defines the URL path that will be listened for.
	 */
	String pathMapping() default "";

	/**
	 * Defines the response type.
	 */
	String mimeType() default "application/json";

	/**
	 * Defines the {@link ResponseTransformer} that will be used to convert the
	 * response in the desired format.
	 */
	Class<? extends ResponseTransformer> responseTransformer() default PlainTExtResponseTransformer.class;
}
