package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Set;

import at.spot.core.infrastructure.exception.DuplicateUserException;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;

public interface UserService {

	void createUser(String userId) throws DuplicateUserException;

	User getUser(String uid);

	List<User> getAllUsers();

	List<UserGroup> getAllUserGroups();

	UserGroup getUserGroup(String uid);

	Set<UserGroup> getAllGroupsOfUser(String uid);

	boolean isUserInGroup(String userUid, String groupUid);

}
