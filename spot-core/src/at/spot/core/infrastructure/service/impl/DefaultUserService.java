package at.spot.core.infrastructure.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.DuplicateUserException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;
import at.spot.core.persistence.exception.ModelNotUniqueException;

@Service
public class DefaultUserService extends AbstractService implements UserService<User, UserGroup> {

	@Autowired
	protected ModelService modelService;

	@Override
	public User createUser(final Class<User> type, final String userId) throws DuplicateUserException {
		final User user = modelService.create(type);
		user.uid = userId;

		try {
			modelService.save(user);
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			throw new DuplicateUserException();
		}

		return user;
	}

	@Override
	public User getUser(final String uid) {
		final Map<String, Comparable<?>> params = new HashMap<>();
		params.put("uid", uid);

		return modelService.get(User.class, params);
	}

	@Override
	public UserGroup getUserGroup(final String uid) {
		final Map<String, Comparable<?>> params = new HashMap<>();
		params.put("uid", uid);

		return modelService.get(UserGroup.class, params);
	}

	@Override
	public boolean isUserInGroup(final String userUid, final String groupUid) {
		return false;
	}

	@Override
	public Set<UserGroup> getAllGroupsOfUser(final String uid) {
		return new HashSet<UserGroup>(getUser(uid).groups);
	}

	@Override
	public List<User> getAllUsers() {
		return modelService.getAll(User.class);
	}

	@Override
	public List<UserGroup> getAllUserGroups() {
		return modelService.getAll(UserGroup.class);
	}
}
