package io.spotnext.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spotnext.infrastructure.maven.xml.DatabaseColumnType;

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

	/**
	 * @return if the property is writable
	 */
	boolean writable() default DEFAULT_WRITABLE;

	/**
	 * @return if the property is readable
	 */
	boolean readable() default DEFAULT_READABLE;

	/**
	 * The uniqueness of the item is defined by the sum of ALL properties that are
	 * unique (compound constraint).
	 * 
	 * @return if the given property is unique
	 */
	boolean unique() default DEFAULT_UNIQUE;

	/**
	 * @return if the property can be written only during initial creation of the
	 *         item.
	 */
	boolean initial() default DEFAULT_INITIAL;

	/**
	 * The value provider implementation that provides the actualy property value
	 * (instead of the persistence layer).
	 * 
	 * @return the bean name ofthe value provider
	 */
	String itemValueProvider() default "";

	/**
	 * The column type the persistence layer is instructed to use instead of the
	 * default value. Keep in mind that the actual property type and the column type
	 * have to be compatible.
	 * 
	 * @return the database column type
	 */
	DatabaseColumnType columnType() default DatabaseColumnType.DEFAULT;
}
