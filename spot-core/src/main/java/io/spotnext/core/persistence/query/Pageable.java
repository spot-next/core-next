package io.spotnext.core.persistence.query;

import java.util.List;

public interface Pageable<T> {

	/**
	 * @return the result data.
	 */
	List<T> getResults();

	/**
	 * @return the current page.
	 */
	int getPage();

	/**
	 * @return the total count of the actual result data. It can never be null.
	 */
	long getCount();

	/**
	 * @return the actual page size (independent of the actual result size)
	 */
	int getPageSize();

	/**
	 * @return the total count of the underlying (unpaged) result or null, if this valu is not available.
	 */
	Long getTotalCount();

}