package io.spotnext.core.infrastructure.exception;

/**
 * <p>AuthenticationException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class AuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for AuthenticationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public AuthenticationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for AuthenticationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public AuthenticationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
