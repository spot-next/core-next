package at.spot.core.infrastructure.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a class as "item type". This is necessary to allow persistence of
 * objects.
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemType {

	/**
	 * This is the bean name of the item.
	 */
	String typeCode();
}
