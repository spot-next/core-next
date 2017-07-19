package at.spot.core.persistence.exception;

public class ModelNotUniqueException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelNotUniqueException(final String message) {
		super(message);
	}

	public ModelNotUniqueException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelNotUniqueException(final Throwable rootCause) {
		super(rootCause);
	}

	public ModelNotUniqueException() {
		super();
	}
}
