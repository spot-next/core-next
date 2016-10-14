package at.spot.core.infrastructure.exception;

public class ModelNotFoundException extends Throwable {
	private static final long serialVersionUID = 1L;

	public ModelNotFoundException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelNotFoundException(Throwable rootCause) {
		super(rootCause);
	}
}
