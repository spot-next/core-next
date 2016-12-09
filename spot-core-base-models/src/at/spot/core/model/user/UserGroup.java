package at.spot.core.model.user;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;

@ItemType
public class UserGroup extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public UserGroup parent;

	@Property(isReference = true)
	public List<User> users;

	@Property(isReference = true)
	public List<UserGroup> subGroups;
}
