package at.spot.core.model.relation;

import at.spot.core.model.OneToManyRelation;
import at.spot.core.model.user.Address;
import at.spot.core.model.user.User;

public class UserGroupToUserRelation extends OneToManyRelation<Address, User> {
	private static final long serialVersionUID = 1L;

}
