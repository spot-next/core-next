package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.model.Item;

public interface ItemLoadInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has been loaded from the persistence layer. Further processing
	 * can be done here.
	 * 
	 * @param item
	 *            The loaded item
	 * @throws ItemInterceptorException
	 *             If thrown the item instantiation will be cancelled.
	 */
	void onLoad(T item) throws ItemInterceptorException;
}
