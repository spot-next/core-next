package io.spotnext.core.infrastructure.service;

import java.util.List;
import java.util.Set;

import io.spotnext.core.infrastructure.exception.CannotCreateUserException;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

/**
 * <p>UserService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface UserService<U extends User, G extends UserGroup> {

	/**
	 * Creates an {@link io.spotnext.itemtype.core.user.User} object, only setting the given userId.
	 *
	 * @param userId a {@link java.lang.String} object.
	 * @return the newly created {@link io.spotnext.itemtype.core.user.User}.
	 * @throws io.spotnext.core.infrastructure.exception.CannotCreateUserException
	 * @param type a {@link java.lang.Class} object.
	 */
	U createUser(Class<U> type, String userId) throws CannotCreateUserException;

	/**
	 * Creates an {@link io.spotnext.itemtype.core.user.User} object, only setting the given userId and
	 * password (using {@link io.spotnext.core.security.service.AuthenticationService#setPassword(User, String)}).
	 *
	 * @param userId a {@link java.lang.String} object.
	 * @return the newly created {@link io.spotnext.itemtype.core.user.User}.
	 * @throws io.spotnext.core.infrastructure.exception.CannotCreateUserException
	 * @param type a {@link java.lang.Class} object.
	 * @param password a {@link java.lang.String} object.
	 */
	U createUser(Class<U> type, String userId, String password) throws CannotCreateUserException;

	/**
	 * <p>getUser.</p>
	 *
	 * @param uid
	 *            the user's uid
	 * @return the given user or null, if the user is not found.
	 */
	U getUser(String uid);

	/**
	 * <p>getAllUsers.</p>
	 *
	 * @return all available {@link io.spotnext.itemtype.core.user.User}s (even subtypes).
	 */
	List<U> getAllUsers();

	/**
	 * <p>getAllUserGroups.</p>
	 *
	 * @return all available {@link io.spotnext.itemtype.core.user.UserGroup}s (even subtypes).
	 */
	List<G> getAllUserGroups();

	/**
	 * Get user group with the given id.
	 *
	 * @param uid a {@link java.lang.String} object.
	 * @return the {@link io.spotnext.itemtype.core.user.UserGroup}
	 */
	G getUserGroup(String uid);

	/**
	 * <p>getAllGroupsOfUser.</p>
	 *
	 * @param uid a {@link java.lang.String} object.
	 * @return a {@link java.util.Set} object.
	 */
	Set<G> getAllGroupsOfUser(String uid);

	/**
	 * <p>isUserInGroup.</p>
	 *
	 * @param userUid a {@link java.lang.String} object.
	 * @param groupUid a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	boolean isUserInGroup(String userUid, String groupUid);

	/**
	 * Returns the current user in the session.
	 *
	 * @return a U object.
	 */
	U getCurrentUser();

	/**
	 * Sets the given user as the current session user.
	 *
	 * @param user a U object.
	 */
	void setCurrentUser(U user);

	/**
	 * Returns true if there is no user registered in the current session.
	 *
	 * @return a boolean.
	 */
	boolean isCurrentUserAnonymous();

}
