package at.spot.core.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.spot.core.support.exception.CannotInvokeException;

/**
 * The dynamic object works similar like a javscript object. You can store any
 * object and retrieve it again - although without type-safety.
 *
 *
 */
public class DynamicObject {
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	protected static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DATE_FORMAT).create();

	protected Map<String, Object> properties = new HashMap<>();

	/**
	 * Sets the given value for the property.
	 * 
	 * @param propertyName
	 * @param value
	 * @return the object
	 */
	public DynamicObject prop(String propertyName, Object value) {
		properties.put(propertyName, value);
		return this;
	}

	/**
	 * Returns the value of the given property or null.
	 * 
	 * @param propertyName
	 * @return
	 */
	public Object prop(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * Invoke (= run) a {@link Callable} object and return the return value. If
	 * 
	 * @param propertyName
	 * @return
	 */
	public <R extends Object> R invoke(String propertyName) throws CannotInvokeException {
		Object callable = properties.get(propertyName);

		try {
			if (callable != null && callable instanceof Callable) {
				return (R) ((Callable<?>) callable).call();
			} else {
				throw new CannotInvokeException(String.format("Method %s not found.", propertyName));
			}
		} catch (Exception e) {
			throw new CannotInvokeException(String.format("Error invoking method %s.", propertyName), e);
		}
	}

	/**
	 * Returns the object representation as JSON.
	 */
	@Override
	public String toString() {
		return GSON.toJson(properties);
	}
}
