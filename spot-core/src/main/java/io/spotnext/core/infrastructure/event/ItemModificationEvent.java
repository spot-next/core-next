package io.spotnext.core.infrastructure.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import io.spotnext.core.types.Item;

/**
 * <p>ItemModificationEvent class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemModificationEvent<T extends Item> extends ApplicationEvent implements ResolvableTypeProvider {
	private static final long serialVersionUID = 1L;

	private final ModificationType modificationType;

	/**
	 * <p>Constructor for ItemModificationEvent.</p>
	 *
	 * @param source a T object.
	 * @param modificationType a {@link io.spotnext.core.infrastructure.event.ItemModificationEvent.ModificationType} object.
	 */
	public ItemModificationEvent(final T source, final ModificationType modificationType) {
		super(source);
		this.modificationType = modificationType;
	}

	/**
	 * <p>Getter for the field <code>modificationType</code>.</p>
	 *
	 * @return a {@link io.spotnext.core.infrastructure.event.ItemModificationEvent.ModificationType} object.
	 */
	public ModificationType getModificationType() {
		return modificationType;
	}

	/**
	 * <p>getItem.</p>
	 *
	 * @return a T object.
	 */
	public T getItem() {
		return (T) getSource();
	}

	public static enum ModificationType {
		CREATE, LOAD, SAVE, REMOVE
	}

	/** {@inheritDoc} */
	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(source));
	}
}
