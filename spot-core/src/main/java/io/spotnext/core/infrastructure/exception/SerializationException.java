package io.spotnext.core.infrastructure.exception;

public class SerializationException extends Exception {
	private static final long serialVersionUID = 1L;

	public SerializationException(final String message) {
		super(message);
	}

	public SerializationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public SerializationException(final Throwable rootCause) {
		super(rootCause);
	}
}
