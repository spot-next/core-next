package io.spotnext.core.security.strategy;

/**
 * <p>PasswordEncryptionStrategy interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface PasswordEncryptionStrategy {
	/**
	 * Encrypts the given password. Depending on the implementation, this might
	 * be a simple hash function or a very complex algorithm.
	 *
	 * @param rawPassword a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	String encryptPassword(String rawPassword);
}
