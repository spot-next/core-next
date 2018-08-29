package io.spotnext.core.infrastructure.exception;

import io.spotnext.core.persistence.exception.ModelNotUniqueException;

/**
 * <p>CannotCreateUserException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class CannotCreateUserException extends ModelNotUniqueException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for CannotCreateUserException.</p>
	 */
	public CannotCreateUserException() {
		super("A user with this uid already exists.");
	}

	/**
	 * <p>Constructor for CannotCreateUserException.</p>
	 *
	 * @param e a {@link java.lang.Exception} object.
	 */
	public CannotCreateUserException(Exception e) {
		super(String.format("A user with this uid already exists. Reason: %s", e.getMessage()));
	}
}
