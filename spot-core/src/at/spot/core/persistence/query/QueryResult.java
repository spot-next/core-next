package at.spot.core.persistence.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.spot.core.model.Item;

public class QueryResult<T extends Item> {
	private List<T> results = new ArrayList<>();
	private final int pageSize;
	private final int page;

	public QueryResult(final List<T> results, final int page, final int pageSize) {
		this.results = Collections.unmodifiableList(results);
		this.page = page;
		this.pageSize = pageSize;
	}

	public List<T> getResult() {
		return results;
	}

	public long count() {
		return results.size();
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPage() {
		return page;
	}
}
