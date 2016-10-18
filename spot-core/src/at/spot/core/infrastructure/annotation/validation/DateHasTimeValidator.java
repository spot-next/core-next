package at.spot.core.infrastructure.annotation.validation;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Checks a given {@link Date} object if it has a time component (= time not 00:00:00).
 * 
 * @See {@link DateHasTime} validation annotation
 */
public class DateHasTimeValidator implements Validator, ConstraintValidator<DateHasTime, Date> {

	private String message;

	public boolean supports(Class<?> paramClass) {
		return Date.class.equals(paramClass);
	}

	public void validate(Object date, Errors errors) {
		if (!isValid((Date) date))
			errors.rejectValue(message, "notset");
	}

	public void initialize(DateHasTime annotation) {
		this.message = annotation.message();
	}

	public boolean isValid(Date date, ConstraintValidatorContext paramConstraintValidatorContext) {
		return isValid(date);
	}

	protected boolean isValid(Date date) {
		return (date != null && !DateUtils.truncate(date, Calendar.DAY_OF_MONTH).equals(date));
	}
}