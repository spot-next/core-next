package io.spotnext.core.infrastructure.exception;

public class AuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AuthenticationException(final String message) {
		super(message);
	}

	public AuthenticationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
