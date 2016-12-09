package at.spot.core.model.user;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;

@ItemType
public class User extends Principal {

	private static final long serialVersionUID = 1L;

	// @Property(isReference = true)
	// public List<UserGroup> groups;

	@Property
	public String password;

	@Property
	public DateTime birthDate;

	@Property
	public String emailAddress;

	// @OneToManyRelation(referenceProperty = "owner")
	// @Property
	// public List<Address> addresses;

	// = new OneToManyRelation<>(ArrayList.class, this, "addresses",
	// Address.class,
	// "owner");
}
