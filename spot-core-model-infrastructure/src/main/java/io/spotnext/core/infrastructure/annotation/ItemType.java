package io.spotnext.core.infrastructure.annotation;

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
	 * 
	 * @return the type code of the annotated item type
	 */
	String typeCode();

	/**
	 * Determines if the annotated item type can be persisted by the persistence
	 * framework.
	 *
	 * @return if the given type is persistable.
	 */
	boolean persistable() default true;
}
