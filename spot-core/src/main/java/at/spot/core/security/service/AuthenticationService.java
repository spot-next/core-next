package at.spot.core.security.service;

import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.security.strategy.PasswordEncryptionStrategy;
import at.spot.itemtype.core.user.User;

public interface AuthenticationService {

	/**
	 * Fetches a user by the given uid and password. If the password is passed
	 * in plain text, the default {@link PasswordEncryptionStrategy} will be
	 * used to encrypt it (for comparison reasons). <br />
	 * <br />
	 * If the user's password has been generated with a different
	 * {@link PasswordEncryptionStrategy} it's not possible anymore, to
	 * authenticate the user. In this case, the password has to be reset.
	 * 
	 * @param uid
	 * @param password
	 * @param isEncrypted
	 */
	User getAuthenticatedUser(final String uid, final String password, boolean isEncrypted);

	/**
	 * Encrypts the given plain text password with the default
	 * {@link PasswordEncryptionStrategy} and stores it in the given
	 * {@link User} model.
	 * 
	 * @param user
	 * @param plainPassword
	 * @throws ModelSaveException
	 */
	void setPassword(final User user, final String plainPassword) throws ModelSaveException;

	/**
	 * Encrypts the given plain text password with the default
	 * {@link PasswordEncryptionStrategy}.
	 * 
	 * @param plainPassword
	 * @throws ModelSaveException
	 */
	String encryptPassword(String plainPassword);
}
