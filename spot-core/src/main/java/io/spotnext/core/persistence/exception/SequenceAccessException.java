package io.spotnext.core.persistence.exception;

public class SequenceAccessException extends Exception {
	private static final long serialVersionUID = 1L;

	public SequenceAccessException(final String message) {
		super(message);
	}

	public SequenceAccessException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public SequenceAccessException(final Throwable rootCause) {
		super(rootCause);
	}

	public SequenceAccessException() {
		super();
	}
}
