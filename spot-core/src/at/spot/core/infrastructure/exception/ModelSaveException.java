package at.spot.core.infrastructure.exception;

public class ModelSaveException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelSaveException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelSaveException(Throwable rootCause) {
		super(rootCause);
	}
}
