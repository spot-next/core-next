package io.spotnext.commerce.exception;

public class OrderCancellationException extends Exception {

	private static final long serialVersionUID = 1L;

	public OrderCancellationException(final String message) {
		super(message);
	}

	public OrderCancellationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
