package at.spot.core.persistence.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Query<T> {
	protected final Class<T> resultClass;
	protected int limit = 0;
	protected int page = 0;
	protected int pageSize = Integer.MAX_VALUE;
	protected final List<String> eagerFetchRelationProperties = new ArrayList<>();
	protected boolean eagerFetchRelations = false;
	protected boolean ignoreCache = false;
	protected boolean clearCaches = false;

	/**
	 * @param resultClass
	 *            the type of the result.
	 */
	public Query(final Class<T> resultClass) {
		this.resultClass = resultClass;
	}

	public boolean isClearCaches() {
		return clearCaches;
	}

	public void setClearCaches(final boolean clearCaches) {
		this.clearCaches = clearCaches;
	}

	public int getPage() {
		return page;
	}

	/**
	 * Set the page of the result data.
	 */
	public void setPage(final int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size for pagination.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getEagerFetchRelationProperties() {
		return eagerFetchRelationProperties;
	}

	public boolean isIgnoreCache() {
		return ignoreCache;
	}

	public void setIgnoreCache(final boolean ignoreCache) {
		this.ignoreCache = ignoreCache;
	}

	/**
	 * Defines which relation properties should be eagerly fetched. This can be
	 * helpful if those properties are being accessed for sure as it reduces the
	 * amount of database queries at the cost of memory usage.
	 */
	public void setEagerFetchRelationProperties(final String... eagerFetchRelationProperties) {
		if (eagerFetchRelationProperties != null) {
			this.eagerFetchRelationProperties.addAll(Arrays.asList(eagerFetchRelationProperties));
		}
	}

	/**
	 * @see #setEagerFetchRelationProperties(String...)
	 */
	public void setEagerFetchRelationProperties(final List<String> eagerFetchRelations) {
		this.eagerFetchRelationProperties.addAll(eagerFetchRelations);
	}

	public boolean isEagerFetchRelations() {
		return eagerFetchRelations;
	}

	/**
	 * Enable this to eagerly fetch ALL relation properties (item references) in
	 * one query. This overrides the {@link #EagerFetchRelationProperties}
	 * property. This can reduce stress on the database, although it increases
	 * memory usage as all data is loaded at once.
	 */
	public void setEagerFetchRelations(final boolean eagerFetchRelations) {
		this.eagerFetchRelations = eagerFetchRelations;
	}

	public Class<T> getResultClass() {
		return resultClass;
	}

	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the result limit.
	 */
	public void setLimit(final int limit) {
		this.limit = limit;
	}

}