package at.spot.core.persistence.exception;

public class CannotCreateModelProxyException extends Exception {
	private static final long serialVersionUID = 1L;

	public CannotCreateModelProxyException(final String message) {
		super(message);
	}

	public CannotCreateModelProxyException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public CannotCreateModelProxyException(final Throwable rootCause) {
		super(rootCause);
	}

	public CannotCreateModelProxyException() {
		super();
	}
}
