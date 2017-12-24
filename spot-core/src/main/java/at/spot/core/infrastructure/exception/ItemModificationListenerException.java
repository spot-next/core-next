package at.spot.core.infrastructure.exception;

public class ItemModificationListenerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ItemModificationListenerException(final String message) {
		super(message);
	}

	public ItemModificationListenerException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ItemModificationListenerException(final Throwable rootCause) {
		super(rootCause);
	}
}
