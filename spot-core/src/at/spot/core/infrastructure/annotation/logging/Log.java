package at.spot.core.infrastructure.annotation.logging;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.type.LogLevel;

@Target({ FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
	boolean before() default true;

	boolean after() default false;

	boolean measureTime() default false;

	LogLevel logLevel() default LogLevel.INFO;

	String message() default "";

	String[] messageArguments() default {};
}
