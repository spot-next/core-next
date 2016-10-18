package at.spot.core.model.user;

import java.util.ArrayList;
import java.util.List;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;

@ItemType
public class UserGroup extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public UserGroup parent;

	@Property(isReference = true)
	public final List<User> users = new ArrayList<>();
}
