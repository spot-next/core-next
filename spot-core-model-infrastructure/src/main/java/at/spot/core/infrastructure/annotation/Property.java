package at.spot.core.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.maven.xml.DatabaseColumnType;

/**
 * Defines an item property. Without this annotation, the values are not stored
 * in the database.
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	boolean DEFAULT_WRITABLE = true;
	boolean DEFAULT_READABLE = true;
	boolean DEFAULT_UNIQUE = false;
	boolean DEFAULT_INITIAL = false;
	boolean DEFAULT_IS_REFERENCE = false;

	boolean writable() default DEFAULT_WRITABLE;

	boolean readable() default DEFAULT_READABLE;

	boolean unique() default DEFAULT_UNIQUE;

	boolean initial() default DEFAULT_INITIAL;

	/**
	 * The value provider implementation that provides the actualy property value
	 * (instead of the persistence layer).
	 */
	String itemValueProvider() default "";

	/**
	 * The column type the persistence layer is instructed to use instead of the
	 * default value. Keep in mind that the actual property type and the column type
	 * have to be compatible.
	 */
	DatabaseColumnType columnType() default DatabaseColumnType.DEFAULT;
}
