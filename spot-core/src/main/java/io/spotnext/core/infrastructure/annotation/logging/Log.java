package io.spotnext.core.infrastructure.annotation.logging;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spotnext.core.infrastructure.support.LogLevel;

@Target({ FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
	boolean before() default true;

	boolean after() default false;

	boolean measureTime() default false;

	LogLevel logLevel() default LogLevel.INFO;

	/**
	 * Logs the given message. The following placeholders are supported:
	 * <ul>
	 * <li>classSimpleName</li>
	 * <li>className</li>
	 * <li>timestamp</li>
	 * </ul>
	 * 
	 * Example: "This is a logging message from classs $className"
	 */
	String message() default "";

	String[] messageArguments() default {};
}
