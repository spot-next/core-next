package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ModelCreationException;
import at.spot.core.types.Item;

public interface ItemCreateInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The newly created item has been initialized from the persistence layer.
	 * Further initialization (eg. default values) can be set here.
	 * 
	 * @param item
	 *            the newly instantiated item instance
	 * @throws ModelCreationException
	 *             if thrown the item instantiation will be cancelled
	 */
	void onCreate(T item) throws ModelCreationException;
}
