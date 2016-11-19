package at.spot.core.infrastructure.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ModelValidationException extends ConstraintViolationException {

	private static final long serialVersionUID = 1L;

	public ModelValidationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
		super(constraintViolations);
	}

	public ModelValidationException(final String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
		super(message, constraintViolations);
	}
}
