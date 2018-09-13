package io.spotnext.infrastructure.type;

import io.spotnext.infrastructure.annotation.ItemType;
import io.spotnext.infrastructure.annotation.Property;
import io.spotnext.infrastructure.type.Item;

@ItemType(typeCode = "keyvaluepair")
public abstract class KeyValuePair<K, V> extends Item {
	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public K key;

	@Property
	public V value;

}
