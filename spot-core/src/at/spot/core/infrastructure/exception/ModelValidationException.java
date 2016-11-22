package at.spot.core.infrastructure.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;

public class ModelValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	private final Set<? extends ConstraintViolation<?>> constraintViolations;

	public ModelValidationException(final Set<? extends ConstraintViolation<?>> constraintViolations) {
		this(null, constraintViolations);
	}

	public ModelValidationException(final String message,
			final Set<? extends ConstraintViolation<?>> constraintViolations) {

		super(message);
		this.constraintViolations = constraintViolations;
	}

	public Set<? extends ConstraintViolation<?>> getConstraintViolations() {
		return constraintViolations;
	}
}
