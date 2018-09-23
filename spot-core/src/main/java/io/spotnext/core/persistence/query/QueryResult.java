package io.spotnext.core.persistence.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * This object holds the results of a persistence query.
 * 
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class QueryResult<T> {
	private final List<T> results;
	private final int pageSize;
	private final int page;
	private final int resultCount;

	/**
	 * @param results
	 *            the result list
	 * @param page
	 *            the current page of the paged result
	 * @param pageSize
	 *            the page size
	 */
	public QueryResult(final List<T> results, final int page, final int pageSize) {
		if (results != null) {
			this.results = results;
		} else {
			this.results = Collections.emptyList();
		}

		this.resultCount = this.results.size();
		this.page = page;
		this.pageSize = pageSize;
	}

	/**
	 * @return the results.
	 */
	public List<T> getResultList() {
		return results;
	}

	/**
	 * @return a stream of the results.
	 */
	public Stream<T> getResultStream() {
		return results.stream();
	}

	/**
	 * @return returns true if the result is empty.
	 */
	public boolean isEmpty() {
		return resultCount == 0;
	}

	/**
	 * @return the current page size of the result.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the current page of the paged result.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @return the amount of result objects.
	 */
	public int getResultCount() {
		return resultCount;
	}

}
