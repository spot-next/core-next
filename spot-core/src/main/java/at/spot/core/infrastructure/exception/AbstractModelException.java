package at.spot.core.infrastructure.exception;

public abstract class AbstractModelException extends Exception {
	private static final long serialVersionUID = 1L;

	public AbstractModelException(final String message) {
		super(message);
	}

	public AbstractModelException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public AbstractModelException(final Throwable rootCause) {
		super(rootCause);
	}
}
