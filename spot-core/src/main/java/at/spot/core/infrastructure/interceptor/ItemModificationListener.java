package at.spot.core.infrastructure.interceptor;

import org.springframework.beans.factory.annotation.Required;

import at.spot.core.infrastructure.exception.ItemModificationListenerException;
import at.spot.core.model.Item;

public interface ItemModificationListener<T extends Item> {
	void onEvent(T item) throws ItemModificationListenerException;

	/**
	 * Returns the item type class this strategy is handling.
	 */
	Class<T> getItemType();

	/**
	 * Sets the item type this listener is responsible for.
	 */
	@Required
	void setItemType(Class<T> itemType);
}
