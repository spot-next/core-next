package io.spotnext.core.persistence.exception;

public class QueryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public QueryException(final String message) {
		super(message);
	}

	public QueryException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	public QueryException(final Throwable rootCause) {
		super(rootCause);
	}

	public QueryException() {
		super();
	}
}
