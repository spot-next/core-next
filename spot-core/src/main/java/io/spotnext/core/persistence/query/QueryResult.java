package io.spotnext.core.persistence.query;

import java.util.List;
import java.util.stream.Stream;

public class QueryResult<T> {
	private List<T> results;
	private final int pageSize;
	private final int page;

	public QueryResult(final List<T> results, final int page, final int pageSize) {
		this.results = results;
		this.page = page;
		this.pageSize = pageSize;
	}

	public List<T> getResultList() {
		return results;
	}

	public Stream<T> getResultStream() {
		return results.stream();
	}

	public long count() {
		return results.size();
	}

	public boolean isEmpty() {
		return count() == 0;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPage() {
		return page;
	}
}
