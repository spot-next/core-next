package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.types.Item;

public interface ItemRemoveInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * This interceptor entry point is called before the item is removed.
	 * 
	 * @param item
	 *            The item to be removed item
	 * @throws ItemInterceptorException
	 *             If thrown the item will not be removed
	 */
	void onRemove(T item) throws ItemInterceptorException;
}
