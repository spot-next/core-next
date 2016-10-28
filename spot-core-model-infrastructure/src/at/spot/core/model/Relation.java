package at.spot.core.model;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;

@ItemType
public abstract class Relation<SOURCE extends Item, TARGET extends Item> extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public SOURCE source;

	@Property(unique = true)
	public TARGET target;

}
