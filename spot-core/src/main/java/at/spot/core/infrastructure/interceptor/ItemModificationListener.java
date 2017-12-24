package at.spot.core.infrastructure.interceptor;

import at.spot.core.infrastructure.exception.ItemModificationListenerException;
import at.spot.core.model.Item;

public interface ItemModificationListener {
	<T extends Item> void onEvent(T item) throws ItemModificationListenerException;
}
