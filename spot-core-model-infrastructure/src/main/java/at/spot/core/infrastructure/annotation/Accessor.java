package at.spot.core.infrastructure.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.type.AccessorType;

/**
 * Marks a method as an accessor to a item property.
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessor {
	/**
	 * The kind of accessor type.
	 * 
	 * @return
	 */
	AccessorType type();

	/**
	 * The name of the class field.
	 */
	String propertyName();

	/**
	 * If the type is a collection, set or list this contains the element type.
	 */
	String elementType() default "";

	/**
	 * If the type is a map, this contains the key type.
	 */
	String keyType() default "";

	/**
	 * If the type is a map, this contains the value type.
	 */
	String valueType() default "";
}
