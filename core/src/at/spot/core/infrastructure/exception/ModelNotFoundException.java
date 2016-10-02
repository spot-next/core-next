package at.spot.core.infrastructure.exception;

public class ModelNotFoundException extends Throwable {
	private static final long serialVersionUID = 1L;

	public ModelNotFoundException(Throwable rootCause) {
		super(rootCause);
	}
}
