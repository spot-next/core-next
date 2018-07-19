package at.spot.core.persistence.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.spot.core.persistence.query.lambda.SerializablePredicate;

import at.spot.core.types.Item;

public class LambdaQuery<T extends Item> extends Query<T> {

	private final List<SerializablePredicate<T>> filters = new ArrayList<>();

	public LambdaQuery(final Class<T> resultClass) {
		super(resultClass);
	}

	public LambdaQuery<T> filter(final SerializablePredicate<T> filter) {
		filters.add(filter);
		return this;
	}

	public LambdaQuery<T> limit(final int limit) {
		this.limit = limit;
		return this;
	}

	public List<SerializablePredicate<T>> getFilters() {
		return Collections.unmodifiableList(filters);
	}

}
