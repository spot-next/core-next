package at.spot.core.persistence.query;

import java.util.List;

import at.spot.core.model.Item;

public interface QueryResult {
	<T extends Item> List<T> getResult();

	<T extends Item> List<T> count();
}
