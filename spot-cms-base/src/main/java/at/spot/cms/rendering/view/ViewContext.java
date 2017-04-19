package at.spot.cms.rendering.view;

import java.util.HashMap;
import java.util.Map;

public class ViewContext {
	private final Map<String, Object> context = new HashMap<>();

	public void add(final String property, final Object value) {
		context.put(property, value);
	}

	public Object get(final String property) {
		return context.get(property);
	}
}
