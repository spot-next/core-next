package io.spotnext.core.support.exception;

public class CannotInvokeException extends Exception {
	private static final long serialVersionUID = 1L;

	public CannotInvokeException(String message) {
		super(message);
	}

	public CannotInvokeException(Throwable rootCause) {
		super(rootCause);
	}

	public CannotInvokeException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}
