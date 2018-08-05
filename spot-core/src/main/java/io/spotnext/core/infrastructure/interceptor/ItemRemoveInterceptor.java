package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.types.Item;

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
