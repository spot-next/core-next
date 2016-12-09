package at.spot.core.infrastructure.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an item property. Without this annotation, the values are not stored
 * in the database.
 */
@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	String accessor() default "";

	boolean writable() default true;

	boolean readable() default true;

	boolean unique() default false;

	boolean initial() default false;

	boolean isReference() default false;

	String itemValueProvider() default "";

}
