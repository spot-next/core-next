package at.spot.core.infrastructure.exception;

public class UnknownTypeException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnknownTypeException(final String message) {
		super(message);
	}

	public UnknownTypeException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public UnknownTypeException(final Throwable rootCause) {
		super(rootCause);
	}
}
