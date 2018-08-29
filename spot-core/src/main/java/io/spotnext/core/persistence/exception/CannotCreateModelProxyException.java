package io.spotnext.core.persistence.exception;

/**
 * <p>CannotCreateModelProxyException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class CannotCreateModelProxyException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for CannotCreateModelProxyException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public CannotCreateModelProxyException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for CannotCreateModelProxyException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public CannotCreateModelProxyException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for CannotCreateModelProxyException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public CannotCreateModelProxyException(final Throwable rootCause) {
		super(rootCause);
	}

	/**
	 * <p>Constructor for CannotCreateModelProxyException.</p>
	 */
	public CannotCreateModelProxyException() {
		super();
	}
}
