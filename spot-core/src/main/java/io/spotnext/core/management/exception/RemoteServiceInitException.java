package io.spotnext.core.management.exception;

/**
 * <p>RemoteServiceInitException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class RemoteServiceInitException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for RemoteServiceInitException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public RemoteServiceInitException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * <p>Constructor for RemoteServiceInitException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public RemoteServiceInitException(String message) {
		super(message);
	}
}
