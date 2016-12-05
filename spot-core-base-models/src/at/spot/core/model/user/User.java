package at.spot.core.model.user;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.type.collection.ObservableList;

@ItemType
public class User extends Principal {

	private static final long serialVersionUID = 1L;

	@Property(isReference = true)
	public final List<UserGroup> groups = new ObservableList<UserGroup>(ArrayList.class, this, "groups");

	@Property
	public String password;

	@Property
	public DateTime birthDate;

	@Property
	public String emailAddress;
}
