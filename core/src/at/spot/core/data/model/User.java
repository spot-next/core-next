package at.spot.core.data.model;

import java.util.List;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;

@Type
public class User extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public List<UserGroup> groups;
}
