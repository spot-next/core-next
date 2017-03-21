package at.spot.core.model.user;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.ItemTypeConstants;

@ItemType(typeCode = ItemTypeConstants.USER)
public class User extends Principal {
	private static final long serialVersionUID = 1L;

	@Relation(type = RelationType.ManyToMany, referencedType = UserGroup.class, mappedTo = "users")
	@Property(isReference = true)
	public List<UserGroup> groups;

	@Property
	public String password;

	public String emailAddress;

	@Relation(type = RelationType.OneToMany, referencedType = Address.class, mappedTo = "owner")
	@Property
	public List<Address> addresses;

	public List<UserGroup> getGroups() {
		return groups;
	}

	public void setGroups(final List<UserGroup> groups) {
		this.groups = groups;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(final List<Address> addresses) {
		this.addresses = addresses;
	}
}
