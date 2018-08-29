package io.spotnext.core.persistence.exception;

import io.spotnext.core.infrastructure.exception.AbstractModelException;

/**
 * <p>ModelNotUniqueException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelNotUniqueException extends AbstractModelException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ModelNotUniqueException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ModelNotUniqueException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ModelNotUniqueException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelNotUniqueException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ModelNotUniqueException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModelNotUniqueException(final Throwable rootCause) {
		super(rootCause);
	}
}
