package io.spotnext.core.model;

import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.types.Item;

@ItemType(typeCode = ItemTypeConstants.KEY_VALUE_PAIR)
public abstract class KeyValuePair<K, V> extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public K key;

	@Property
	public V value;

}
