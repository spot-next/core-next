package io.spotnext.core.infrastructure.exception;

public class ItemInterceptorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ItemInterceptorException(final String message) {
		super(message);
	}

	public ItemInterceptorException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ItemInterceptorException(final Throwable rootCause) {
		super(rootCause);
	}
}
