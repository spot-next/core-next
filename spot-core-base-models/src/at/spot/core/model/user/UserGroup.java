package at.spot.core.model.user;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;

@ItemType
public class UserGroup extends Principal {

	private static final long serialVersionUID = 1L;

	@Property
	public UserGroup parent;

	@Property
	public List<User> users;

	@Relation(type = RelationType.OneToMany, referencedType = UserGroup.class, mappedTo = "parent")
	@Property
	public List<UserGroup> subGroups;
}
