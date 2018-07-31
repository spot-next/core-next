package at.spot.core.infrastructure.exception;

public class ImportException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ImportException(final String message) {
		super(message);
	}

	public ImportException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ImportException(final Throwable rootCause) {
		super(rootCause);
	}
}
