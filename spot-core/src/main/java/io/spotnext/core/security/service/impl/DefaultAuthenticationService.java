package io.spotnext.core.security.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.core.security.strategy.PasswordEncryptionStrategy;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import io.spotnext.support.util.ValidationUtil;

/**
 * <p>DefaultAuthenticationService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultAuthenticationService extends AbstractService implements AuthenticationService {

	@Autowired
	protected PasswordEncryptionStrategy passwordEncryptionStrategy;

	@Autowired
	protected UserService<User, UserGroup> userService;

	/** {@inheritDoc} */
	@Cacheable("misc")
	@Override
	public User getAuthenticatedUser(final String name, final String password, final boolean isEncrypted) {
		ValidationUtil.validateNotBlankOrEmpty("Password cannot be blank", password);

		String encryptedPassword = password;

		if (!isEncrypted) {
			encryptedPassword = encryptPassword(password);
		}

		final User user = userService.getUser(name);

		if (user != null && StringUtils.equals(user.getPassword(), encryptedPassword)) {
			return user;
		}

		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setPassword(final User user, final String plainPassword) throws ModelSaveException {
		user.setPassword(encryptPassword(plainPassword));

		try {
			modelService.save(user);
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			throw new ModelSaveException(e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String encryptPassword(final String plainPassword) {
		return passwordEncryptionStrategy.encryptPassword(plainPassword);
	}
}
