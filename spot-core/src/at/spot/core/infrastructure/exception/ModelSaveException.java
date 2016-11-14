package at.spot.core.infrastructure.exception;

public class ModelSaveException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelSaveException(final String message) {
		super(message);
	}

	public ModelSaveException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelSaveException(final Throwable rootCause) {
		super(rootCause);
	}
}
