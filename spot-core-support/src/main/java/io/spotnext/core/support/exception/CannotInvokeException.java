package io.spotnext.core.support.exception;

/**
 * <p>CannotInvokeException class.</p>
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
public class CannotInvokeException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for CannotInvokeException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public CannotInvokeException(String message) {
		super(message);
	}

	/**
	 * <p>Constructor for CannotInvokeException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public CannotInvokeException(Throwable rootCause) {
		super(rootCause);
	}

	/**
	 * <p>Constructor for CannotInvokeException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public CannotInvokeException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}
