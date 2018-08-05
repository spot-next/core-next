package io.spotnext.core.infrastructure.exception;

public class DeserializationException extends Exception {
	private static final long serialVersionUID = 1L;

	public DeserializationException(final String message) {
		super(message);
	}

	public DeserializationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public DeserializationException(final Throwable rootCause) {
		super(rootCause);
	}
}
