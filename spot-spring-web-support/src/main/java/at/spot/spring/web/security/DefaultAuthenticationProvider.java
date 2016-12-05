package at.spot.spring.web.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.model.user.Principal;
import at.spot.core.model.user.User;
import at.spot.core.security.service.AuthenticationService;
import at.spot.spring.web.security.exception.AuthenticationException;

/**
 * Checks if there is a {@link User} with the given password. If the
 * {@link Principal#uid} equals the value of the config property
 * {@link #ADMIN_USER_NAME_KEY} then the admin role will be granted. You can set
 * this key in your *.properties.
 *
 */
public class DefaultAuthenticationProvider implements AuthenticationProvider {

	public static String ADMIN_USER_NAME_KEY = "security.authentication.admin.username";
	public static String DEFAULT_ADMIN_USER_NAME = "admin";

	@Autowired
	protected AuthenticationService authenticationService;

	@Autowired
	protected ConfigurationService configurationService;

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
		final String name = authentication.getName();
		final String password = authentication.getCredentials().toString();

		final User user = authenticationService.getAuthenticatedUser(name, password);

		if (user != null) {
			final List<GrantedAuthority> grantedAuths = new ArrayList<>();

			if (isAdminUser(user)) {
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			} else {
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
			}

			final Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);

			return auth;
		} else {
			throw new AuthenticationException("User could not be authenticated.");
		}
	}

	public boolean isAdminUser(final User user) {
		final String adminUserName = configurationService.getString(ADMIN_USER_NAME_KEY, DEFAULT_ADMIN_USER_NAME);

		return StringUtils.equals(user.uid, adminUserName);
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
