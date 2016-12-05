package at.spot.core.infrastructure.exception;

import at.spot.core.persistence.exception.ModelNotUniqueException;

public class DuplicateUserException extends ModelNotUniqueException {
	private static final long serialVersionUID = 1L;

	public DuplicateUserException() {
		super("A user with this uid already exists.");
	}
}
