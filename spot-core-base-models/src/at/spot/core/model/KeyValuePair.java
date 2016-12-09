package at.spot.core.model;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;

@ItemType
public abstract class KeyValuePair<K, V> extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public K key;

	@Property
	public V value;

}
