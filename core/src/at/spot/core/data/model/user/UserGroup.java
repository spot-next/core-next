package at.spot.core.data.model.user;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.ItemType;

@ItemType
public class UserGroup extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public UserGroup parent;
}
