package io.spotnext.core.infrastructure.exception;

/**
 * <p>ItemInterceptorException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemInterceptorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ItemInterceptorException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ItemInterceptorException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ItemInterceptorException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ItemInterceptorException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ItemInterceptorException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ItemInterceptorException(final Throwable rootCause) {
		super(rootCause);
	}
}
