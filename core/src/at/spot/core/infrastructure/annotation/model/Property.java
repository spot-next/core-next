package at.spot.core.infrastructure.annotation.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	String accessor() default "";

	boolean writable() default true;

	boolean readable() default true;

	boolean unique() default false;
	
	boolean initial() default false;

	boolean isReference() default false;

	String itemValueProvider() default "";

}
