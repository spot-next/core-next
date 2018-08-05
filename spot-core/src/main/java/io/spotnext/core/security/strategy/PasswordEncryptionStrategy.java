package io.spotnext.core.security.strategy;

public interface PasswordEncryptionStrategy {
	/**
	 * Encrypts the given password. Depending on the implementation, this might
	 * be a simple hash function or a very complex algorithm.
	 * 
	 * @param rawPassword
	 */
	String encryptPassword(String rawPassword);
}
