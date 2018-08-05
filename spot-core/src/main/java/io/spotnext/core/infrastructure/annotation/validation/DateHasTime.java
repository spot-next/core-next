package io.spotnext.core.infrastructure.annotation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.springmodules.validation.bean.conf.loader.annotation.handler.ValidationRule;

/**
 * Checks a given {@link Date} object if it has a time component (= time not 00:00:00).
 */
@Documented
@Constraint(validatedBy = DateHasTimeValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ValidationRule
public @interface DateHasTime {

	String message() default "{DateHasTime}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
