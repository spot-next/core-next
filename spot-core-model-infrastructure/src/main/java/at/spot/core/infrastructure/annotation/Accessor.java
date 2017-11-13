package at.spot.core.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.type.AccessorType;

/**
 * Marks a method as an accessor to a item property.
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessor {
	AccessorType type();
}
