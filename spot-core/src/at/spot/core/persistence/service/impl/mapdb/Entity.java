package at.spot.core.persistence.service.impl.mapdb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import at.spot.core.infrastructure.type.PK;

public class Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, Object> properties = new HashMap<>();
	private PK pk;

	public PK getPK() {
		return this.pk;
	}

	public void setPK(PK pk) {
		this.pk = pk;
	}

	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public Map<String, Object> getProperties() {
		return properties;
	}
}
