package at.spot.core.data.model.user;

import java.util.ArrayList;
import java.util.List;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;

@ItemType
public class User extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public List<UserGroup> groups = new ArrayList<>();
}
