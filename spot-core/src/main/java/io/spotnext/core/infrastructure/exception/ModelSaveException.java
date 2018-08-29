package io.spotnext.core.infrastructure.exception;

/**
 * <p>ModelSaveException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelSaveException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ModelSaveException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ModelSaveException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ModelSaveException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelSaveException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ModelSaveException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelSaveException(final Throwable rootCause) {
		super(rootCause);
	}
}
