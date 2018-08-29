package io.spotnext.core.security.strategy.impl;

import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.stereotype.Service;

import io.spotnext.core.security.strategy.PasswordEncryptionStrategy;

/**
 * <p>MD5PasswordEncryptionStrategy class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class MD5PasswordEncryptionStrategy implements PasswordEncryptionStrategy {

	/**
	 * {@inheritDoc}
	 *
	 * Creates an MD5 hash out of the given raw password.
	 */
	@Override
	public String encryptPassword(final String rawPassword) {
		return MD5.digest(rawPassword);
	}
}
