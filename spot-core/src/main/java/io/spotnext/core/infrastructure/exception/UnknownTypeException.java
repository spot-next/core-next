package io.spotnext.core.infrastructure.exception;

/**
 * <p>UnknownTypeException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class UnknownTypeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for UnknownTypeException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public UnknownTypeException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for UnknownTypeException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public UnknownTypeException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for UnknownTypeException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public UnknownTypeException(final Throwable rootCause) {
		super(rootCause);
	}
}
