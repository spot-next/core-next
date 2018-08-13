package io.spotnext.core.infrastructure.event;

import org.springframework.context.ApplicationEvent;

import io.spotnext.core.types.Item;

public class ItemModificationEvent<T extends Item> extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	private final ModificationType modificationType;

	public ItemModificationEvent(final T source, final ModificationType modificationType) {
		super(source);
		this.modificationType = modificationType;
	}

	public ModificationType getModificationType() {
		return modificationType;
	}

	public T getModifiedItem() {
		return (T) getSource();
	}

	public static enum ModificationType {
		CREATE, LOAD, SAVE, REMOVE
	}
}
