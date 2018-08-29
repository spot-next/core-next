package io.spotnext.spring.web.exception;

/**
 * <p>ServerStartupException class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ServerStartupException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for ServerStartupException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public ServerStartupException(String message) {
		super(message);
	}

	/**
	 * <p>Constructor for ServerStartupException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param rootCause a {@link java.lang.Throwable} object.
	 */
	public ServerStartupException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}
