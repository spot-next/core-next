package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.types.Item;

public interface ItemPrepareInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has been validated and can now be post-processed before it is
	 * persisted by the persistence layer.
	 * 
	 * @param item
	 *            The validated item
	 * @throws ItemInterceptorException
	 *             If thrown the item will not be persisted.
	 */
	void onPrepare(T item) throws ItemInterceptorException;
}
