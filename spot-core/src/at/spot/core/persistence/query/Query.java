package at.spot.core.persistence.query;

import at.spot.core.model.Item;

public interface Query {

	static <T extends Item> Select select(Class<T> type) {
		return null;
	}

	// <T extends Item> SelectAll selectAll(T item);
}
