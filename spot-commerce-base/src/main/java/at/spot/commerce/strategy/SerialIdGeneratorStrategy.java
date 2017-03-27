package at.spot.commerce.strategy;

import at.spot.core.model.Item;

public interface SerialIdGeneratorStrategy<T extends Item> {

	/**
	 * Generates a serial id. Typically for orders/carts a custom implementation
	 * is used.
	 * 
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	String generateId(T item);
}
