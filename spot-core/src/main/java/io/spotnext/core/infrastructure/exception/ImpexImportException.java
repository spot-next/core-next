package io.spotnext.core.infrastructure.exception;

/**
 * <p>ImpexImportException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ImpexImportException extends ImportException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ImpexImportException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ImpexImportException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ImpexImportException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ImpexImportException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ImpexImportException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ImpexImportException(final Throwable rootCause) {
		super(rootCause);
	}
}
