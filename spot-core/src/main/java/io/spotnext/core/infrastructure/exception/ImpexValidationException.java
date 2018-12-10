package io.spotnext.core.infrastructure.exception;

/**
 * ImpexValidationException
 * 
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ImpexValidationException extends ImportException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ImpexImportException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ImpexValidationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ImpexImportException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ImpexValidationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ImpexImportException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ImpexValidationException(final Throwable rootCause) {
		super(rootCause);
	}
}
