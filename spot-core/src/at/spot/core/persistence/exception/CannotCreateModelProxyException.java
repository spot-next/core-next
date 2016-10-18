package at.spot.core.persistence.exception;

public class CannotCreateModelProxyException extends Exception {
	private static final long serialVersionUID = 1L;

	public CannotCreateModelProxyException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	public CannotCreateModelProxyException(Throwable rootCause) {
		super(rootCause);
	}
}
