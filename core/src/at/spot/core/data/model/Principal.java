package at.spot.core.data.model;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;
import at.spot.core.infrastructure.annotation.model.Unique;

@Type
public abstract class Principal extends Item {

	private static final long serialVersionUID = 1L;

	@Unique
	@Property(unique = true)
	public String uid;

	@Property
	public String name;

	@Property
	public Relation<Principal, User> users;

}
