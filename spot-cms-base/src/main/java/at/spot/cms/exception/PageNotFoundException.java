package at.spot.cms.exception;

public class PageNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public PageNotFoundException(final String message) {
		super(message);
	}

	public PageNotFoundException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
