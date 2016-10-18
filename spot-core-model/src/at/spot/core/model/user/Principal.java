package at.spot.core.model.user;

import at.spot.core.model.Item;
import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Unique;

@ItemType
public abstract class Principal extends Item {

	private static final long serialVersionUID = 1L;

	@Unique
	@Property(unique = true)
	public String uid;

	@Property
	public String name;

}
