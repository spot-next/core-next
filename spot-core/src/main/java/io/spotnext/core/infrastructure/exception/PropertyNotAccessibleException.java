package io.spotnext.core.infrastructure.exception;

/**
 * <p>PropertyNotAccessibleException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class PropertyNotAccessibleException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for PropertyNotAccessibleException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public PropertyNotAccessibleException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for PropertyNotAccessibleException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public PropertyNotAccessibleException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for PropertyNotAccessibleException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public PropertyNotAccessibleException(final Throwable rootCause) {
		super(rootCause);
	}
}
