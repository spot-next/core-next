package io.spotnext.core.infrastructure.exception;

/**
 * <p>Abstract AbstractModelException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractModelException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for AbstractModelException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public AbstractModelException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for AbstractModelException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public AbstractModelException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for AbstractModelException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public AbstractModelException(final Throwable rootCause) {
		super(rootCause);
	}
}
