package io.spotnext.core.infrastructure.exception;

/**
 * <p>DeserializationException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class DeserializationException extends SerializationException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for DeserializationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public DeserializationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for DeserializationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public DeserializationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for DeserializationException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public DeserializationException(final Throwable rootCause) {
		super(rootCause);
	}
}
