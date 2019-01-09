package io.spotnext.infrastructure;

/**
 * <p>SequenceAccessException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SequenceAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for SequenceAccessException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public SequenceAccessException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for SequenceAccessException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public SequenceAccessException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for SequenceAccessException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public SequenceAccessException(final Throwable rootCause) {
		super(rootCause);
	}

	/**
	 * <p>Constructor for SequenceAccessException.</p>
	 */
	public SequenceAccessException() {
		super();
	}
}
