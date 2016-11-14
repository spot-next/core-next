package at.spot.core.infrastructure.exception;

public class PropertyNotAccessibleException extends Throwable {
	private static final long serialVersionUID = 1L;

	public PropertyNotAccessibleException(final String message) {
		super(message);
	}

	public PropertyNotAccessibleException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public PropertyNotAccessibleException(final Throwable rootCause) {
		super(rootCause);
	}
}
