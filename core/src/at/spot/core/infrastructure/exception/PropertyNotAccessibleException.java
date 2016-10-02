package at.spot.core.infrastructure.exception;

public class PropertyNotAccessibleException extends Throwable {
	private static final long serialVersionUID = 1L;

	public PropertyNotAccessibleException(Throwable rootCause) {
		super(rootCause);
	}
}
