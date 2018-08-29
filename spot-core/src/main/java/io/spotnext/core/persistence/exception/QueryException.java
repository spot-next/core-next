package io.spotnext.core.persistence.exception;

/**
 * <p>QueryException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class QueryException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for QueryException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public QueryException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for QueryException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public QueryException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for QueryException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public QueryException(final Throwable rootCause) {
		super(rootCause);
	}

	/**
	 * <p>Constructor for QueryException.</p>
	 */
	public QueryException() {
		super();
	}
}
