package at.spot.core.security.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.security.service.AuthenticationService;
import at.spot.core.security.service.AuthenticationService;
import at.spot.core.security.strategy.PasswordEncryptionStrategy;

@Service
public class DefaultAuthenticationService extends AbstractService implements AuthenticationService {

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected PasswordEncryptionStrategy passwordEncryptionStrategy;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Override
	public User getAuthenticatedUser(final String name, final String password, final boolean isEncrypted) {
		String encryptedPassword = password;

		if (!isEncrypted) {
			encryptedPassword = encryptPassword(password);
		}

		final User user = userService.getUser(name);

		if (user != null) {
			if (StringUtils.equals(user.password, encryptedPassword)) {
				return user;
			}
		}

		return null;
	}

	@Override
	public void setPassword(final User user, final String plainPassword) throws ModelSaveException {
		user.password = encryptPassword(plainPassword);

		try {
			modelService.save(user);
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			throw new ModelSaveException(e.getMessage(), e);
		}
	}

	@Override
	public String encryptPassword(final String plainPassword) {
		return passwordEncryptionStrategy.encryptPassword(plainPassword);
	}
}
