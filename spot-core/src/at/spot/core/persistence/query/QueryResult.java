package at.spot.core.persistence.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.spot.core.model.Item;

public class QueryResult<T extends Item> {
	List<T> results = new ArrayList<>();

	public QueryResult(final List<T> results) {
		this.results = Collections.unmodifiableList(results);
	}

	public List<T> getResult() {
		return results;
	}

	public long count() {
		return results.size();
	}
}
