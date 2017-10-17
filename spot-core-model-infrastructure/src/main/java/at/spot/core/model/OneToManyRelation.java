package at.spot.core.model;

import at.spot.core.infrastructure.annotation.Property;

public abstract class OneToManyRelation<SOURCE extends Item, TARGET extends Item> extends Relation<SOURCE, TARGET> {
	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	protected SOURCE source;

}
