package io.spotnext.core.persistence.exception;

import io.spotnext.core.infrastructure.exception.AbstractModelException;

public class ModelNotUniqueException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	public ModelNotUniqueException(final String message) {
		super(message);
	}

	public ModelNotUniqueException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public ModelNotUniqueException(final Throwable rootCause) {
		super(rootCause);
	}
}
