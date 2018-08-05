package io.spotnext.core.infrastructure.exception;

public class BootstrapException extends Exception {
	private static final long serialVersionUID = 1L;

	public BootstrapException(final String message) {
		super(message);
	}

	public BootstrapException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}
}
