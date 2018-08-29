package io.spotnext.core.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.spotnext.core.support.exception.CannotInvokeException;

/**
 * The dynamic object works similar like a javascript object. You can store any
 * object and retrieve it again - although without type-safety.
 *
 * @since 1.0
 */
public class DynamicObject {
	/** Constant <code>DATE_FORMAT="yyyy-MM-dd HH:mm:ss"</code> */
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** Constant <code>GSON</code> */
	protected static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DATE_FORMAT).create();

	protected Map<String, Object> properties = new HashMap<>();

	/**
	 * Sets the given value for the property.
	 *
	 * @param propertyName a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
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
	 * Invoke (= run) a {@link java.util.concurrent.Callable} object and return the return value. If
	 *
	 * @param propertyName a {@link java.lang.String} object.
	 * @param <R> a R object.
	 * @return a R object.
	 * @throws io.spotnext.core.support.exception.CannotInvokeException if any.
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
		return GSON.toJson(properties);
	}
}
