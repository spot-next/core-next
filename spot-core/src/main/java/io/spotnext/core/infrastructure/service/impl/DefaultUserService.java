package io.spotnext.core.infrastructure.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.spotnext.core.constant.CoreConstants;
import io.spotnext.core.infrastructure.exception.CannotCreateUserException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.itemtype.core.beans.UserData;
import io.spotnext.itemtype.core.user.Principal;
import io.spotnext.itemtype.core.user.PrincipalGroup;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

/**
 * <p>
 * DefaultUserService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultUserService<U extends User, G extends UserGroup> extends AbstractService
		implements UserService<U, G> {

	public static final UserData DEFAULT_USER = new UserData();
	static {
		DEFAULT_USER.setUid(CoreConstants.ANONYMOUS_USER_UID);
	}

	@Autowired
	protected SessionService sessionService;

	@Autowired
	protected AuthenticationService authenticationService;

	/** {@inheritDoc} */
	@Override
	public U createUser(final Class<U> type, final String userId) throws CannotCreateUserException {
		return createUser(type, userId, null);
	}

	/** {@inheritDoc} */
	@Override
	public U createUser(final Class<U> type, final String userId, final String password)
			throws CannotCreateUserException {

		final U user = modelService.create(type);
		user.setUid(userId);

		try {
			if (StringUtils.isNotBlank(password)) {
				authenticationService.setPassword(user, password);
			}

			modelService.save(user);
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			throw new CannotCreateUserException(e);
		}

		return user;
	}

	/** {@inheritDoc} */
	@Override
	public U getUser(final String uid) {
		final Map<String, Object> params = new HashMap<>();
		params.put(Principal.PROPERTY_UID, uid);

		return modelService.get(getUserType(), params);
	}

	/** {@inheritDoc} */
	@Override
	public G getUserGroup(final String uid) {
		final Map<String, Object> params = new HashMap<>();
		params.put(UserGroup.PROPERTY_UID, uid);

		return modelService.get(getUserGroupType(), params);
	}

	/**
	 * {@inheritDoc} As iterating over all groups recursively is a cost-intensive task, the results are cached.
	 */
	@Cacheable("misc")
	@Override
	public boolean isUserInGroup(final String userUid, final String groupUid) {
		User user = getUser(userUid);

		if (user != null && user.getGroups() != null) {
			final List<PrincipalGroup> groupsToCheck = new ArrayList<>(user.getGroups());

			for (int x = 0; x < groupsToCheck.size(); x++) {
				final PrincipalGroup currentGroup = groupsToCheck.get(x);

				// check if groups match
				if (StringUtils.equals(groupUid, currentGroup.getUid())) {
					return true;
				}

				// if not, add all subgroups to the list of groups to check
				groupsToCheck.addAll(groupsToCheck.size(), currentGroup.getGroups());
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<G> getAllGroupsOfUser(final String uid) {
		final Set<G> groups = new HashSet<>();

		for (final PrincipalGroup g : getUser(uid).getGroups()) {
			if (g instanceof UserGroup) {
				groups.add((G) g);
			}
		}

		return groups;
	}

	/** {@inheritDoc} */
	@Override
	public List<U> getAllUsers() {
		return modelService.getAll(getUserType(), null);
	}

	/** {@inheritDoc} */
	@Override
	public List<G> getAllUserGroups() {
		return modelService.getAll(getUserGroupType(), null);
	}

	protected Class<U> getUserType() {
		final Class<U> userType = (Class<U>) getApplicationContext().getBean(User.TYPECODE).getClass();
		return userType;
	}

	protected Class<G> getUserGroupType() {
		final Class<G> userGroupType = (Class<G>) getApplicationContext().getBean(UserGroup.TYPECODE)
				.getClass();

		return userGroupType;
	}

	/** {@inheritDoc} */
	@Override
	public void setCurrentUser(final U user) {
		final Session session = sessionService.getCurrentSession();

		final UserData userData = new UserData();
		userData.setUid(user.getUid());

		session.setAttribute(CoreConstants.SESSION_KEY_CURRENT_USER, userData);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCurrentUserAnonymous() {
		return getCurrentUser() == null;
	}

	/** {@inheritDoc} */
	@Override
	public UserData getCurrentUser() {
		final Session session = sessionService.getCurrentSession();

		if (session != null) {
			final Optional<UserData> user = session.<UserData>attribute(CoreConstants.SESSION_KEY_CURRENT_USER);

			return user.orElse(DEFAULT_USER);
		} else {
			Logger.warn("No session is set up.");
		}

		return DEFAULT_USER;
	}
}
