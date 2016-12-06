package at.spot.core.infrastructure.service.impl;

import java.util.HashMap;
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
public class DefaultUserService extends AbstractService implements UserService {

	@Autowired
	protected ModelService modelService;

	@Override
	public <U extends User> U createUser(final Class<U> type, final String userId) throws DuplicateUserException {
		final U user = modelService.create(type);
		user.uid = userId;

		try {
			modelService.save(user);
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			throw new DuplicateUserException();
		}

		return user;
	}

	@Override
	public <U extends User> U getUser(final String uid) {
		final Map<String, Comparable<?>> params = new HashMap<>();
		params.put("uid", uid);

		return (U) modelService.get(User.class, params);
	}

	@Override
	public <U extends UserGroup> U getUserGroup(final String uid) {
		final Map<String, Comparable<?>> params = new HashMap<>();
		params.put("uid", uid);

		return (U) modelService.get(UserGroup.class, params);
	}

	@Override
	public boolean isUserInGroup(final String userUid, final String groupUid) {
		return false;
	}

	@Override
	public Set<UserGroup> getAllGroupsOfUser(final String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends User> List<U> getAllUsers() {
		return (List<U>) modelService.getAll(User.class);
	}

	@Override
	public <U extends UserGroup> List<U> getAllUserGroups() {
		return (List<U>) modelService.getAll(UserGroup.class);
	}
}
