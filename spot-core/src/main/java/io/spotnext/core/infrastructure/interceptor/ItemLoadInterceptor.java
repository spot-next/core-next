package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.types.Item;

/**
 * <p>ItemLoadInterceptor interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ItemLoadInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The given item has been loaded from the persistence layer. Further processing
	 * can be done here.
	 *
	 * @param item
	 *            The loaded item
	 * @throws io.spotnext.core.infrastructure.exception.ItemInterceptorException
	 *             If thrown the item instantiation will be cancelled.
	 */
	void onLoad(T item) throws ItemInterceptorException;
}
