package io.spotnext.instrumentation.transformer;

import java.lang.instrument.IllegalClassFormatException;

public class IllegalClassTransformationException extends IllegalClassFormatException {
	private static final long serialVersionUID = 1L;

	protected Throwable rootCause;

	public IllegalClassTransformationException(final String message) {
		super(message);
	}

	public IllegalClassTransformationException(final String message, final Throwable rootCause) {
		this(message);
		this.rootCause = rootCause;
	}

	public Throwable getRootCause() {
		return rootCause;
	}

	public void setRootCause(final Throwable rootCause) {
		this.rootCause = rootCause;
	}
}
