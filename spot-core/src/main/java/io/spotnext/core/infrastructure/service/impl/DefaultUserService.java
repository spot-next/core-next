package io.spotnext.core.infrastructure.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.constant.CoreConstants;
import io.spotnext.core.infrastructure.exception.CannotCreateUserException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.model.ItemTypeConstants;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.itemtype.core.user.Principal;
import io.spotnext.itemtype.core.user.PrincipalGroup;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

/**
 * <p>DefaultUserService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultUserService<U extends User, G extends UserGroup> extends AbstractService
		implements UserService<U, G> {

	@Autowired
	protected ModelService modelService;

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
		user.setId(userId);

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
		params.put(Principal.PROPERTY_ID, uid);

		return modelService.get(getUserType(), params);
	}

	/** {@inheritDoc} */
	@Override
	public G getUserGroup(final String uid) {
		final Map<String, Object> params = new HashMap<>();
		params.put("id", uid);

		return modelService.get(getUserGroupType(), params);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isUserInGroup(final String userUid, final String groupUid) {
		return false;
	}

	/** {@inheritDoc} */
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
		final Class<U> userType = (Class<U>) getApplicationContext().getBean(ItemTypeConstants.USER).getClass();
		return userType;
	}

	protected Class<G> getUserGroupType() {
		final Class<G> userGroupType = (Class<G>) getApplicationContext().getBean(ItemTypeConstants.USER_GROUP)
				.getClass();

		return userGroupType;
	}

	/** {@inheritDoc} */
	@Override
	public void setCurrentUser(final U user) {
		final Session session = sessionService.getCurrentSession();

		session.setAttribute(CoreConstants.SESSION_KEY_CURRENT_USER, user);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCurrentUserAnonymous() {
		return getCurrentUser() == null;
	}

	/** {@inheritDoc} */
	@Override
	public U getCurrentUser() {
		final Session session = sessionService.getCurrentSession();

		if (session != null) {
			U user = (U) session.getAttribute(CoreConstants.SESSION_KEY_CURRENT_USER);

			if (user != null) {
				try {
					// load from database, because entities/models might not be threadsafe
					user = (U) modelService.get(user.getClass(), user.getPk());
				} catch (final ModelNotFoundException e) {
					loggingService.warn("Current session user was invalid - removed it.");
					sessionService.closeSession(session.getId());
				}
			}

			return (U) user;
		} else {
			loggingService.warn("No session is set up.");
		}

		return null;
	}
}
