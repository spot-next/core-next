package io.spotnext.core.infrastructure.exception;

/**
 * <p>ValueResolverException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ValueResolverException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ValueResolverException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ValueResolverException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ValueResolverException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ValueResolverException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ValueResolverException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ValueResolverException(final Throwable rootCause) {
		super(rootCause);
	}
}
