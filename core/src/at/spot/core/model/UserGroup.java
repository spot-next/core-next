package at.spot.core.model;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public class UserGroup extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public UserGroup parent;
}
