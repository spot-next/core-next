package at.spot.core.model.relation;

import at.spot.core.model.ManyToManyRelation;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;

public class AddressToUserRelation extends ManyToManyRelation<UserGroup, User> {
	private static final long serialVersionUID = 1L;

}
