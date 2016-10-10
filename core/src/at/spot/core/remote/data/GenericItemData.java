package at.spot.core.remote.data;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a generic DTO implementation.
 */
public class GenericItemData {

	public Map<String, Object> properties = new HashMap<>();
	public String typeName;
	public String typePackage;
	public String beanName;

	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	public Object getValue(String key) {
		return properties.get(key);
	}
}
