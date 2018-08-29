package io.spotnext.spring.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>UserStatus class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class UserStatus {
	private boolean isAuthenticated = false;
	private String username;
	private String sessionId;
	private String redirectUrl;

	/**
	 * <p>Getter for the field <code>redirectUrl</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 * <p>Setter for the field <code>redirectUrl</code>.</p>
	 *
	 * @param redirectUrl a {@link java.lang.String} object.
	 */
	public void setRedirectUrl(final String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * <p>isAuthenticated.</p>
	 *
	 * @return a boolean.
	 */
	@JsonProperty("isAuthenticated")
	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	/**
	 * <p>setAuthenticated.</p>
	 *
	 * @param isAuthenticated a boolean.
	 */
	public void setAuthenticated(final boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	/**
	 * <p>Getter for the field <code>username</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <p>Setter for the field <code>username</code>.</p>
	 *
	 * @param username a {@link java.lang.String} object.
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * <p>Getter for the field <code>sessionId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * <p>Setter for the field <code>sessionId</code>.</p>
	 *
	 * @param sessionId a {@link java.lang.String} object.
	 */
	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}
}
