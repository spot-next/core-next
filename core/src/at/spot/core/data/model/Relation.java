package at.spot.core.data.model;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public abstract class Relation<S extends Item, T extends Item> extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public S source;

	@Property(unique = true)
	public T target;

}
