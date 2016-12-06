package at.spot.core.infrastructure.service;

import java.util.List;
import java.util.Set;

import at.spot.core.infrastructure.exception.DuplicateUserException;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;

public interface UserService {

	/**
	 * Creates an empty {@link User} object, only setting the given userId.
	 * 
	 * @param userId
	 * @return the newly created {@link User}.
	 * @throws DuplicateUserException
	 */
	<U extends User> U createUser(Class<U> type, String userId) throws DuplicateUserException;

	/**
	 * @param uid
	 *            the user's uid
	 * @return the given user or null, if the user is not found.
	 */
	<U extends User> U getUser(String uid);

	/**
	 * @return all available {@link User}s (even subtypes).
	 */
	<U extends User> List<U> getAllUsers();

	/**
	 * @return all available {@link UserGroup}s (even subtypes).
	 */
	<U extends UserGroup> List<U> getAllUserGroups();

	/**
	 * 
	 * @param uid
	 * @return the {@link UserGroup}
	 */
	<U extends UserGroup> U getUserGroup(String uid);

	<U extends UserGroup> Set<U> getAllGroupsOfUser(String uid);

	boolean isUserInGroup(String userUid, String groupUid);

}
