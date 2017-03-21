package at.spot.core.model.user;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.USER_GROUP)
public class UserGroup extends Principal {

	private static final long serialVersionUID = 1L;

	@Property
	public UserGroup parent;

	@Property
	public List<User> users;

	@Relation(type = RelationType.OneToMany, referencedType = UserGroup.class, mappedTo = "parent")
	@Property
	public List<UserGroup> subGroups;

	public UserGroup getParent() {
		return parent;
	}

	public void setParent(final UserGroup parent) {
		this.parent = parent;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(final List<User> users) {
		this.users = users;
	}

	public List<UserGroup> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(final List<UserGroup> subGroups) {
		this.subGroups = subGroups;
	}

}
