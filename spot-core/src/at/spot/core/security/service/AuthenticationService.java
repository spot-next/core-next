package at.spot.core.security.service;

import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.model.user.User;

public interface AuthenticationService {

	User getAuthenticatedUser(String name, String encryptedPassword);

	void setPassword(final User user, final String rawPassword) throws ModelSaveException;

	String encryptPassword(String rawPassword);
}
