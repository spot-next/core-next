package io.spotnext.core.infrastructure.exception;

/**
 * <p>ModelCreationException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelCreationException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ModelCreationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ModelCreationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ModelCreationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelCreationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ModelCreationException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelCreationException(final Throwable rootCause) {
		super(rootCause);
	}
}
