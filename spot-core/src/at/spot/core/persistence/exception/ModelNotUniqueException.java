package at.spot.core.persistence.exception;

public class ModelNotUniqueException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelNotUniqueException(String message) {
		super(message);
	}

	public ModelNotUniqueException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelNotUniqueException() {
		// TODO Auto-generated constructor stub
	}
}
