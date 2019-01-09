package io.spotnext.commerce.exception;

/**
 * Thrown when no cart with the given id is found.
 */
public class NoSuchCartException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param cartId of the desired cart
	 */
	public NoSuchCartException(String cartId) {
		super("No cart found with id=" + cartId);
	}

}
