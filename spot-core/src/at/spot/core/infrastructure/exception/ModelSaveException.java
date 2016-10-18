package at.spot.core.infrastructure.exception;

public class ModelSaveException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelSaveException(Throwable rootCause) {
		super(rootCause);
	}
}
