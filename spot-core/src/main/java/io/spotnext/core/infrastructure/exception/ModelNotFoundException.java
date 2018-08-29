package io.spotnext.core.infrastructure.exception;

/**
 * <p>ModelNotFoundException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelNotFoundException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ModelNotFoundException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ModelNotFoundException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ModelNotFoundException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelNotFoundException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ModelNotFoundException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelNotFoundException(final Throwable rootCause) {
		super(rootCause);
	}
}
