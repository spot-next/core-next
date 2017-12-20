package at.spot.core.persistence.query;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.spot.core.model.Item;

public class QueryResult<T extends Item> {
	private Stream<T> results;
	private final int pageSize;
	private final int page;

	public QueryResult(final Stream<T> results, final int page, final int pageSize) {
		this.results = results;
		this.page = page;
		this.pageSize = pageSize;
	}

	public List<T> getResultList() {
		return results.collect(Collectors.toList());
	}

	public Stream<T> getResultStream() {
		return results;
	}

	public long count() {
		return results.count();
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPage() {
		return page;
	}
}
