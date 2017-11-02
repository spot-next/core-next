package at.spot.core.infrastructure.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Defines a class as "item type". This is necessary to allow persistence of
 * objects.
 */
@Documented
@Component
@Target(TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Scope(scopeName = "prototype")
public @interface ItemType {

	/**
	 * This is the bean name of the item.
	 */
	String typeCode();

	/**
	 * Specifies that the underlying persistence service should handle this type as
	 * separate entity.
	 */
	boolean separateDeployment() default false;
}
