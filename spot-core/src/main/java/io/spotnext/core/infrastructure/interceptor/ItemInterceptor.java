package io.spotnext.core.infrastructure.interceptor;

import io.spotnext.core.types.Item;

public interface ItemInterceptor<T extends Item> {
	/**
	 * Returns the item type class this strategy is handling.
	 */
	Class<T> getItemType();
}
