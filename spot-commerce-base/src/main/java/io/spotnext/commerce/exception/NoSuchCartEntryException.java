package io.spotnext.commerce.exception;

/**
 * Thrown when no cart entry with the given id is found.
 */
public class NoSuchCartEntryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param cartId of the desired cart
	 */
	public NoSuchCartEntryException(String cartId, int entryNumber) {
		super("No cart entry found with entry number=" + entryNumber + " for cart with id=" + cartId);
	}

}
