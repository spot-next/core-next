package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.infrastructure.exception.ModelCreationException;
import io.spotnext.core.types.Item;

/**
 * <p>ItemCreateInterceptor interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ItemCreateInterceptor<T extends Item> extends ItemInterceptor<T> {
	/**
	 * The newly created item has been initialized from the persistence layer.
	 * Further initialization (eg. default values) can be set here.
	 *
	 * @param item
	 *            the newly instantiated item instance
	 * @throws io.spotnext.core.infrastructure.exception.ModelCreationException
	 *             if thrown the item instantiation will be cancelled
	 */
	void onCreate(T item) throws ModelCreationException;
}
