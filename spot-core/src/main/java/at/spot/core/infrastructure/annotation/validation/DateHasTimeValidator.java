package at.spot.core.infrastructure.annotation.validation;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Checks a given {@link Date} object if it has a time component (= time not
 * 00:00:00).
 * 
 * @see DateHasTime
 */
public class DateHasTimeValidator implements Validator, ConstraintValidator<DateHasTime, Date> {

	private String message;

	@Override
	public boolean supports(final Class<?> paramClass) {
		return Date.class.equals(paramClass);
	}

	@Override
	public void validate(final Object date, final Errors errors) {
		if (!isValid((Date) date))
			errors.rejectValue(message, "notset");
	}

	@Override
	public void initialize(final DateHasTime annotation) {
		this.message = annotation.message();
	}

	@Override
	public boolean isValid(final Date date, final ConstraintValidatorContext paramConstraintValidatorContext) {
		return isValid(date);
	}

	protected boolean isValid(final Date date) {
		return date != null && !DateUtils.truncate(date, Calendar.DAY_OF_MONTH).equals(date);
	}
}