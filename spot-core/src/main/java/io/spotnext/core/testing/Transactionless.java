package io.spotnext.core.testing;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated method doesn't need a transaction (or handles transactions manually).
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
@Inherited
public @interface Transactionless {

}
