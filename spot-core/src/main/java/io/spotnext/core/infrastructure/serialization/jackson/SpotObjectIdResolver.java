package io.spotnext.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

/**
 * Simple implementation of {@link ObjectIdResolver}
 * 
 * @author Pascal Gélinas
 */
public class SpotObjectIdResolver extends SimpleObjectIdResolver {

	@Override
	public void bindItem(IdKey id, Object ob) {
		if (_items == null) {
			super.bindItem(id, ob);
		} else {
			Object old = _items.get(id);
			if (old != null) {
				// 11-Nov-2020, tatu: As per [annotations#180] allow duplicate calls:
				if (old == ob) {
					return;
				}
//				throw new IllegalStateException(
//						"Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
			}
		}
		_items.put(id, ob);
	}

	public ObjectIdResolver newForDeserialization(Object context) {
		// 19-Dec-2014, tatu: Important: must re-create without existing mapping;
		// otherwise bindings leak
		// (and worse, cause unnecessary memory retention)
		return new SpotObjectIdResolver();
	}
}
