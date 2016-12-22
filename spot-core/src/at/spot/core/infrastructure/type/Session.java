package at.spot.core.infrastructure.type;

import java.util.Map;

import at.spot.core.model.user.User;

public class Session {

	protected String id;
	protected User user;
	protected Object rawSession;
	protected Map<String, Object> attributes;

	public Session(final String id, final User user) {
		this.id = id;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public User user() {
		return user;
	}

	public void user(final User user) {
		this.user = user;
	}

	public boolean isAnonymousUser() {
		return user == null;
	}

	public Object attribute(final String name) {
		return attributes.get(name);
	}

	public Object attribute(final String name, final Object value) {
		return attributes.put(name, value);
	}
}
