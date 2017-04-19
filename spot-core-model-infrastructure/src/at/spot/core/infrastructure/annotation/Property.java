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
	public static final boolean DEFAULT_WRITABLE = true;
	public static final boolean DEFAULT_READABLE = true;
	public static final boolean DEFAULT_UNIQUE = false;
	public static final boolean DEFAULT_INITIAL = false;
	public static final boolean DEFAULT_IS_REFERENCE = false;

	boolean writable() default DEFAULT_WRITABLE;

	boolean readable() default DEFAULT_READABLE;

	boolean unique() default DEFAULT_UNIQUE;

	boolean initial() default DEFAULT_INITIAL;

	boolean isReference() default DEFAULT_IS_REFERENCE;

	String itemValueProvider() default "";
}
