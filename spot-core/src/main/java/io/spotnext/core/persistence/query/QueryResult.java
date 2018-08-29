package io.spotnext.core.persistence.query;

import java.util.List;
import java.util.stream.Stream;

/**
 * <p>QueryResult class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class QueryResult<T> {
	private List<T> results;
	private final int pageSize;
	private final int page;

	/**
	 * <p>Constructor for QueryResult.</p>
	 *
	 * @param results a {@link java.util.List} object.
	 * @param page a int.
	 * @param pageSize a int.
	 */
	public QueryResult(final List<T> results, final int page, final int pageSize) {
		this.results = results;
		this.page = page;
		this.pageSize = pageSize;
	}

	/**
	 * <p>getResultList.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<T> getResultList() {
		return results;
	}

	/**
	 * <p>getResultStream.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object.
	 */
	public Stream<T> getResultStream() {
		return results.stream();
	}

	/**
	 * <p>count.</p>
	 *
	 * @return a long.
	 */
	public long count() {
		return results.size();
	}

	/**
	 * <p>isEmpty.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEmpty() {
		return count() == 0;
	}

	/**
	 * <p>Getter for the field <code>pageSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * <p>Getter for the field <code>page</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPage() {
		return page;
	}
}
