package at.spot.core.model;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;

@ItemType
public abstract class KeyValuePair<V> extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public String key;

	@Property
	public V value;

}
