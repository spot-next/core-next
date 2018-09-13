package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemInterceptor interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface ItemInterceptor<T extends Item> {
	/**
	 * Returns the item type class this strategy is handling.
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	Class<T> getItemType();
}
