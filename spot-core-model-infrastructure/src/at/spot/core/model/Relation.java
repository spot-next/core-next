package at.spot.core.model;

import at.spot.core.infrastructure.annotation.ItemType;

@ItemType
public abstract class Relation<SOURCE extends Item, TARGET extends Item> extends Item {
	private static final long serialVersionUID = 1L;
}
