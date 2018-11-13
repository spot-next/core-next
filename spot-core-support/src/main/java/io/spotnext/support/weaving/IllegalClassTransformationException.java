package io.spotnext.support.weaving;

import java.lang.instrument.IllegalClassFormatException;

/**
 * <p>IllegalClassTransformationException class.</p>
 *
 * @since 1.0
 */
public class IllegalClassTransformationException extends IllegalClassFormatException {
	private static final long serialVersionUID = 1L;

	protected Throwable rootCause;

	/**
	 * <p>Constructor for IllegalClassTransformationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public IllegalClassTransformationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for IllegalClassTransformationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public IllegalClassTransformationException(final String message, final Throwable rootCause) {
		this(message);
		this.rootCause = rootCause;
	}

	/**
	 * <p>Getter for the field <code>rootCause</code>.</p>
	 *
	 * @return a {@link java.lang.Throwable} object.
	 */
	public Throwable getRootCause() {
		return rootCause;
	}

	/**
	 * <p>Setter for the field <code>rootCause</code>.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public void setRootCause(final Throwable rootCause) {
		this.rootCause = rootCause;
	}
}
