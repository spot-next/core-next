package at.spot.core.infrastructure.support;

import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.joda.time.DateTime;

import at.spot.core.model.user.User;

public class Session {

	protected boolean isValid = true;

	protected String id;
	protected User user;
	protected Object rawSession;
	protected Map<String, Object> attributes;
	protected DateTime creationTime = new DateTime();
	protected DateTime lastAccessedTime = new DateTime();

	protected int maxInactiveInterval;

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

	public Object getAttribute(final String name) {
		return attributes.get(name);
	}

	public void setAttribute(final String name, final Object value) {
		attributes.put(name, value);
	}

	public boolean isValid() {
		return isValid;
	}

	public void invalidate() {
		this.isValid = false;
	}

	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<String>(attributes.keySet().iterator());
	}

	public long getCreationTime() {
		return creationTime.toDate().getTime();
	}

	public long getLastAccessedTime() {
		return lastAccessedTime.toDate().getTime();
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void removeAttribute(final String attributeName) {
		attributes.remove(attributeName);
	}

	public void setMaxInactiveInterval(final int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
}
