package io.spotnext.core.infrastructure.annotation.validation;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Checks a given {@link java.util.Date} object if it has a time component (= time not
 * 00:00:00).
 *
 * @see DateHasTime
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class DateHasTimeValidator implements Validator, ConstraintValidator<DateHasTime, Date> {

	private String message;

	/** {@inheritDoc} */
	@Override
	public boolean supports(final Class<?> paramClass) {
		return Date.class.equals(paramClass);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(final Object date, final Errors errors) {
		if (!isValid((Date) date))
			errors.rejectValue(message, "notset");
	}

	/** {@inheritDoc} */
	@Override
	public void initialize(final DateHasTime annotation) {
		this.message = annotation.message();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isValid(final Date date, final ConstraintValidatorContext paramConstraintValidatorContext) {
		return isValid(date);
	}

	protected boolean isValid(final Date date) {
		return date != null && !DateUtils.truncate(date, Calendar.DAY_OF_MONTH).equals(date);
	}
}
