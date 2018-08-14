package io.spotnext.core.infrastructure.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import io.spotnext.core.types.Item;

public class ItemModificationEvent<T extends Item> extends ApplicationEvent implements ResolvableTypeProvider {
	private static final long serialVersionUID = 1L;

	private final ModificationType modificationType;

	public ItemModificationEvent(final T source, final ModificationType modificationType) {
		super(source);
		this.modificationType = modificationType;
	}

	public ModificationType getModificationType() {
		return modificationType;
	}

	public T getItem() {
		return (T) getSource();
	}

	public static enum ModificationType {
		CREATE, LOAD, SAVE, REMOVE
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(source));
	}
}
