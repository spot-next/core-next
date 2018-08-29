package io.spotnext.core.infrastructure.exception;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * <p>ModelValidationException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelValidationException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	private final Set<? extends ConstraintViolation<?>> constraintViolations;

	/**
	 * <p>Constructor for ModelValidationException.</p>
	 *
	 * @param constraintViolations a {@link java.util.Set} object.
	 */
	public ModelValidationException(final Set<? extends ConstraintViolation<?>> constraintViolations) {
		this(null, constraintViolations);
	}

	/**
	 * <p>Constructor for ModelValidationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param constraintViolations a {@link java.util.Set} object.
	 */
	public ModelValidationException(final String message,
			final Set<? extends ConstraintViolation<?>> constraintViolations) {

		super(message);
		this.constraintViolations = constraintViolations;
	}

	/**
	 * <p>Constructor for ModelValidationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ModelValidationException(final String message) {
		this(message, new HashSet<>());
	}

	/**
	 * <p>Getter for the field <code>constraintViolations</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<? extends ConstraintViolation<?>> getConstraintViolations() {
		return constraintViolations;
	}
}
