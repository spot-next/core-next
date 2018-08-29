package io.spotnext.cms.exception;

/**
 * Exception that indicates that a page was not found. May be interpreted as a
 * HTTP 404.
 */
public class PageNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message giving more information about the exception.
	 */
	public PageNotFoundException(final String message) {
		super(message);
	}

	/**
	 * @param message giving more information about the exception.
	 * @param rootCause of the exception
	 */
	public PageNotFoundException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
