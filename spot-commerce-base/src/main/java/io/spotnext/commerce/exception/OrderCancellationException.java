package io.spotnext.commerce.exception;

/**
 * <p>OrderCancellationException class.</p>
 */
public class OrderCancellationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for OrderCancellationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public OrderCancellationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for OrderCancellationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public OrderCancellationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
