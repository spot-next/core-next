package io.spotnext.core.infrastructure.exception;

public class ModuleInitializationException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModuleInitializationException(final String message) {
		super(message);
	}

	public ModuleInitializationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ModuleInitializationException(final Throwable rootCause) {
		super(rootCause);
	}
}
