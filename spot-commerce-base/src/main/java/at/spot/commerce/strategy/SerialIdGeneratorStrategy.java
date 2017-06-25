package at.spot.commerce.strategy;

import at.spot.core.model.Item;

/**
 * Generates a serial id. Typically for orders/carts a custom implementation is
 * used.
 */
public interface SerialIdGeneratorStrategy<T extends Item> {

	/**
	 * Generates a serial id - the returned value depends on the underlying
	 * implementation. It can be a random number or a predefined string..
	 */
	String generateId(T item);
}
