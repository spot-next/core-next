package at.spot.core.persistence.service.impl.mapdb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, Object> properties = new HashMap<>();
	private Long pk;
	private String itemTypeClassName;
	private int uniquenessHash;

	public Entity(Long pk, String itemTypeClassName, int uniquenessHash) {
		this.pk = pk;
		this.itemTypeClassName = itemTypeClassName;
		this.uniquenessHash = uniquenessHash;
	}

	public Long getPK() {
		return this.pk;
	}

	public void setPK(long pk) {
		this.pk = pk;
	}

	public String getItemClassName() {
		return itemTypeClassName;
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

	public int getUniquenessHash() {
		return uniquenessHash;
	}

}
