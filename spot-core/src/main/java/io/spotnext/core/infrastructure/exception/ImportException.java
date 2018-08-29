package io.spotnext.core.infrastructure.exception;

/**
 * <p>ImportException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ImportException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ImportException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ImportException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ImportException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ImportException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ImportException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ImportException(final Throwable rootCause) {
		super(rootCause);
	}
}
