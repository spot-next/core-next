package at.spot.core.infrastructure.exception;

import at.spot.core.persistence.exception.ModelNotUniqueException;

public class CannotCreateUserException extends ModelNotUniqueException {
	private static final long serialVersionUID = 1L;

	public CannotCreateUserException() {
		super("A user with this uid already exists.");
	}
}
