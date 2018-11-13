package io.spotnext.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import io.spotnext.support.exception.CannotInvokeException;

/**
 * The dynamic object works similar like a javascript object. You can store any
 * object and retrieve it again - although without type-safety.
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
public class DynamicObject {
	/** Constant <code>DATE_FORMAT="yyyy-MM-dd HH:mm:ss"</code> */
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** Constant <code>GSON</code> */
	protected Map<String, Object> properties = new HashMap<>();

	/**
	 * Sets the given value for the property.
	 *
	 * @param propertyName a {@link java.lang.String} object.
	 * @param value        a {@link java.lang.Object} object.
	 * @return the object
	 */
	public DynamicObject prop(final String propertyName, final Object value) {
		properties.put(propertyName, value);
		return this;
	}

	/**
	 * Returns the value of the given property or null.
	 *
	 * @param propertyName a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object prop(final String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * Invoke (= run) a {@link Callable} object and return the
	 * return value.
	 *
	 * @param propertyName the property name of the {@link Callable} value
	 * @return the return value of the callable, can be null
	 * @throws CannotInvokeException if the given property is not of type
	 *                               {@link Callable} or any other exception is
	 *                               thrown during invoking the property value
	 */
	public <R extends Object> R invoke(final String propertyName) throws CannotInvokeException {
		final Object callable = properties.get(propertyName);

		try {
			if (callable != null && callable instanceof Callable) {
				return (R) ((Callable<?>) callable).call();
			} else {
				throw new CannotInvokeException(String.format("Method %s not found.", propertyName));
			}
		} catch (final Exception e) {
			throw new CannotInvokeException(String.format("Error invoking method %s.", propertyName), e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns the object representation as JSON.
	 */
	@Override
	public String toString() {
		return new JSONObject(properties).toString();
	}
}
