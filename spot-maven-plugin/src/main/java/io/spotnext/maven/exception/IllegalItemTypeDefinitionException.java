package io.spotnext.maven.exception;

/**
 * <p>IllegalItemTypeDefinitionException class.</p>
 *
 * @since 1.0
 */
public class IllegalItemTypeDefinitionException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for IllegalItemTypeDefinitionException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param cause a {@link java.lang.Throwable} object.
	 */
	public IllegalItemTypeDefinitionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <p>Constructor for IllegalItemTypeDefinitionException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public IllegalItemTypeDefinitionException(String message) {
		super(message);
	}

	/**
	 * <p>Constructor for IllegalItemTypeDefinitionException.</p>
	 *
	 * @param cause a {@link java.lang.Throwable} object.
	 */
	public IllegalItemTypeDefinitionException(Throwable cause) {
		super(cause);
	}
}
