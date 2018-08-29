package io.spotnext.core.infrastructure.exception;

/**
 * <p>ModuleInitializationException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModuleInitializationException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ModuleInitializationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ModuleInitializationException(final String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ModuleInitializationException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModuleInitializationException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for ModuleInitializationException.</p>
	 *
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ModuleInitializationException(final Throwable rootCause) {
		super(rootCause);
	}
}
