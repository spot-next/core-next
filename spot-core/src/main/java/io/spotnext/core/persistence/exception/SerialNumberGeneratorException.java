package io.spotnext.core.persistence.exception;

/**
 * <p>SerialNumberGeneratorException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SerialNumberGeneratorException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for SerialNumberGeneratorException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public SerialNumberGeneratorException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for SerialNumberGeneratorException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public SerialNumberGeneratorException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for SerialNumberGeneratorException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public SerialNumberGeneratorException(final Throwable rootCause) {
		super(rootCause);
	}

	/**
	 * <p>Constructor for SerialNumberGeneratorException.</p>
	 */
	public SerialNumberGeneratorException() {
		super();
	}
}
