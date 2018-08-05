package io.spotnext.core.persistence.exception;

public class SerialNumberGeneratorException extends Exception {
	private static final long serialVersionUID = 1L;

	public SerialNumberGeneratorException(final String message) {
		super(message);
	}

	public SerialNumberGeneratorException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public SerialNumberGeneratorException(final Throwable rootCause) {
		super(rootCause);
	}

	public SerialNumberGeneratorException() {
		super();
	}
}
