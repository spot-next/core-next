package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.types.Item;

public interface ItemPrepareInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * This is called before an item is being validated and saved by the
	 * persistence layer.
	 * 
	 * @param item
	 *            The validated item
	 * @throws ItemInterceptorException
	 *             If thrown the item will not be persisted.
	 */
	void onPrepare(T item) throws ItemInterceptorException;
}
