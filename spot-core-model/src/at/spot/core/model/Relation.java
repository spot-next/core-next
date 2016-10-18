package at.spot.core.model;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;

@ItemType
public abstract class Relation<S extends Item, T extends Item> extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public S source;

	@Property(unique = true)
	public T target;

}
