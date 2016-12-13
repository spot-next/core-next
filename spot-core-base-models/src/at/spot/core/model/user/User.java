package at.spot.core.model.user;

import java.util.List;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;

@ItemType
public class User extends Principal {
	private static final long serialVersionUID = 1L;

	@Relation(type = RelationType.ManyToMany, referencedType = UserGroup.class, mappedTo = "users")
	@Property(isReference = true)
	public List<UserGroup> groups;

	@Property
	public String password;

	@Property
	public DateTime birthDate;

	@Property
	public String emailAddress;

	@Relation(type = RelationType.OneToMany, referencedType = Address.class, mappedTo = "owner")
	@Property
	public List<Address> addresses;
}
