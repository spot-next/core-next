package at.spot.core.infrastructure.interceptor;

import at.spot.core.model.Item;

public interface ItemInterceptor<T extends Item> {
	/**
	 * Returns the item type class this strategy is handling.
	 */
	Class<T> getItemType();
}
