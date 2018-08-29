package io.spotnext.core.infrastructure.http;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.iterators.IteratorEnumeration;

/**
 * <p>Session class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class Session {

	protected boolean isValid = true;

	protected String id;
	protected Date creationTime = new Date();
	protected Date lastAccessedTime = new Date();
	protected final Map<String, Object> attributes = new HashMap<>();

	protected int maxInactiveInterval;

	/**
	 * <p>Constructor for Session.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 */
	public Session(final String id) {
		this.id = id;
	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p>getAttribute.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object getAttribute(final String name) {
		return attributes.get(name);
	}

	/**
	 * <p>setAttribute.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 */
	public void setAttribute(final String name, final Object value) {
		attributes.put(name, value);
	}

	/**
	 * <p>isValid.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * <p>invalidate.</p>
	 */
	public void invalidate() {
		this.isValid = false;
	}

	/**
	 * <p>getAttributeNames.</p>
	 *
	 * @return a {@link java.util.Enumeration} object.
	 */
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<String>(attributes.keySet().iterator());
	}

	/**
	 * <p>Getter for the field <code>creationTime</code>.</p>
	 *
	 * @return a long.
	 */
	public long getCreationTime() {
		return creationTime.getTime();
	}

	/**
	 * <p>Getter for the field <code>lastAccessedTime</code>.</p>
	 *
	 * @return a long.
	 */
	public long getLastAccessedTime() {
		return lastAccessedTime.getTime();
	}

	/**
	 * <p>Getter for the field <code>maxInactiveInterval</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * <p>removeAttribute.</p>
	 *
	 * @param attributeName a {@link java.lang.String} object.
	 */
	public void removeAttribute(final String attributeName) {
		attributes.remove(attributeName);
	}

	/**
	 * <p>Setter for the field <code>maxInactiveInterval</code>.</p>
	 *
	 * @param maxInactiveInterval a int.
	 */
	public void setMaxInactiveInterval(final int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
}
