package io.spotnext.core.infrastructure.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

/**
 * <p>ValidationService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ValidationService {
	/**
	 * Validates JSR-303 annotations of the given object.
	 *
	 * @param object
	 *            to validate
	 * @throws javax.validation.ValidationException
	 * @param <T> a T object.
	 * @return a {@link java.util.Set} object.
	 */
	<T extends Object> Set<ConstraintViolation<T>> validate(T object) throws ValidationException;

	/**
	 * Converts a collection of {@link javax.validation.ConstraintViolation} objects into a
	 * readable message form.
	 *
	 * @param violations
	 *            the violations to process
	 * @return the human-readable string representation
	 */
	String convertToReadableMessage(Set<ConstraintViolation<?>> violations);
}
