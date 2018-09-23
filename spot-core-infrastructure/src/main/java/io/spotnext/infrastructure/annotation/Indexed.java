package io.spotnext.infrastructure.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the field as indexable by the persistence service.
 */
@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {

}
