package io.spotnext.core.security.service;

import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.security.strategy.PasswordEncryptionStrategy;
import io.spotnext.itemtype.core.user.User;

/**
 * <p>AuthenticationService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface AuthenticationService {

	/**
	 * Fetches a user by the given uid and password. If the password is passed
	 * in plain text, the default {@link io.spotnext.core.security.strategy.PasswordEncryptionStrategy} will be
	 * used to encrypt it (for comparison reasons). <br />
	 * <br />
	 * If the user's password has been generated with a different
	 * {@link io.spotnext.core.security.strategy.PasswordEncryptionStrategy} it's not possible anymore, to
	 * authenticate the user. In this case, the password has to be reset.
	 *
	 * @param uid a {@link java.lang.String} object.
	 * @param password a {@link java.lang.String} object.
	 * @param isEncrypted a boolean.
	 * @return a {@link io.spotnext.itemtype.core.user.User} object.
	 */
	User getAuthenticatedUser(final String uid, final String password, boolean isEncrypted);

	/**
	 * Encrypts the given plain text password with the default
	 * {@link io.spotnext.core.security.strategy.PasswordEncryptionStrategy} and stores it in the given
	 * {@link io.spotnext.itemtype.core.user.User} model.
	 *
	 * @param user a {@link io.spotnext.itemtype.core.user.User} object.
	 * @param plainPassword a {@link java.lang.String} object.
	 * @throws io.spotnext.core.infrastructure.exception.ModelSaveException
	 */
	void setPassword(final User user, final String plainPassword) throws ModelSaveException;

	/**
	 * Encrypts the given plain text password with the default
	 * {@link io.spotnext.core.security.strategy.PasswordEncryptionStrategy}.
	 *
	 * @param plainPassword a {@link java.lang.String} object.
	 * @throws ModelSaveException if any.
	 * @return a {@link java.lang.String} object.
	 */
	String encryptPassword(String plainPassword);
}
