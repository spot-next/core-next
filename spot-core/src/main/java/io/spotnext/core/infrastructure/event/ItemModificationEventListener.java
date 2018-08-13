package io.spotnext.core.infrastructure.event;

import org.springframework.context.event.EventListener;

import io.spotnext.core.types.Item;

/**
 * The base application event listener for item modification
 */
public abstract class ItemModificationEventListener<T extends Item>
		extends AbstractEventListener<ItemModificationEvent<T>> {

	@EventListener
	public abstract void handleEvent(ItemModificationEvent<T> event);
}
