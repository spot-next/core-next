package io.spotnext.cms.rendering.view;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ViewContext class.</p>
 */
public class ViewContext {
	private final Map<String, Object> context = new HashMap<>();

	/**
	 * <p>add.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 */
	public void add(final String property, final Object value) {
		context.put(property, value);
	}

	/**
	 * <p>get.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object get(final String property) {
		return context.get(property);
	}
}
