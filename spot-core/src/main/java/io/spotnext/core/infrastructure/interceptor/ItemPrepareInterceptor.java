package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.types.Item;

/**
 * <p>ItemPrepareInterceptor interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ItemPrepareInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * This is called before an item is being validated and saved by the
	 * persistence layer.
	 *
	 * @param item
	 *            The validated item
	 * @throws io.spotnext.core.infrastructure.exception.ItemInterceptorException
	 *             If thrown the item will not be persisted.
	 */
	void onPrepare(T item) throws ItemInterceptorException;
}
