package io.spotnext.core.infrastructure.annotation.logging;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Logger class.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Target({ FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Logs {
	/**
	 * @return the {@link Log} annotations.
	 */
	Log[] value();
}
