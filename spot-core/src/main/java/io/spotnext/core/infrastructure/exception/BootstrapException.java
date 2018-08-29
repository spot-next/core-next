package io.spotnext.core.infrastructure.exception;

/**
 * <p>BootstrapException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class BootstrapException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for BootstrapException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public BootstrapException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for BootstrapException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public BootstrapException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
