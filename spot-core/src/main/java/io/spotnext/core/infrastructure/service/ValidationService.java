package io.spotnext.core.infrastructure.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

public interface ValidationService {
	/**
	 * 
	 * @param object
	 * @throws ValidationException
	 */
	// Errors validate(Object object) throws ValidationException;
	<T extends Object> Set<ConstraintViolation<T>> validate(T object) throws ValidationException;
}
