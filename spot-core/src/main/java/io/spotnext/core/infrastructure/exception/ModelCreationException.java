package io.spotnext.core.infrastructure.exception;

public class ModelCreationException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	public ModelCreationException(final String message) {
		super(message);
	}

	public ModelCreationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelCreationException(final Throwable rootCause) {
		super(rootCause);
	}
}
