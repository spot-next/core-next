package at.spot.spring.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserStatus {
	private boolean isAuthenticated = false;
	private String username;
	private String sessionId;
	private String redirectUrl;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(final String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@JsonProperty("isAuthenticated")
	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public void setAuthenticated(final boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}
}
