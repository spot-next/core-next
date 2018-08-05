package io.spotnext.core.infrastructure.exception;

public class ValueResolverException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ValueResolverException(final String message) {
		super(message);
	}

	public ValueResolverException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ValueResolverException(final Throwable rootCause) {
		super(rootCause);
	}
}
