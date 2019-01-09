package io.spotnext.commerce.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This is a marker interface for variant product options.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface VariantOption {

}
