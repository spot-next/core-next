package io.spotnext.core.infrastructure.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

public interface ValidationService {
	/**
	 * Validates JSR-303 annotations of the given object.
	 * 
	 * @param object
	 *            to validate
	 * @throws ValidationException
	 */
	<T extends Object> Set<ConstraintViolation<T>> validate(T object) throws ValidationException;

	/**
	 * Converts a collection of {@link ConstraintViolation} objects into a
	 * readable message form.
	 * 
	 * @param violations
	 *            the violations to process
	 * @return the human-readable string representation
	 */
	String convertToReadableMessage(Set<ConstraintViolation<?>> violations);
}
