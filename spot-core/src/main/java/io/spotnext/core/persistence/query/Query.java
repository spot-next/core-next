package io.spotnext.core.persistence.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Abstract Query class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class Query<T> {
	protected final Class<T> resultClass;
//	protected int limit = 0;
	protected int page = 0;
	protected int pageSize = 0;
	protected final List<String> eagerFetchRelationProperties = new ArrayList<>();
	protected boolean eagerFetchRelations = false;
	protected boolean ignoreCache = false;
	protected boolean clearCaches = false;
	protected boolean cachable = true;

	/**
	 * <p>Constructor for Query.</p>
	 *
	 * @param resultClass
	 *            the type of the result.
	 */
	public Query(final Class<T> resultClass) {
		this.resultClass = resultClass;
	}

	/**
	 * <p>isClearCaches.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isClearCaches() {
		return clearCaches;
	}

	/**
	 * <p>Setter for the field <code>clearCaches</code>.</p>
	 *
	 * @param clearCaches a boolean.
	 */
	public void setClearCaches(final boolean clearCaches) {
		this.clearCaches = clearCaches;
	}

	/**
	 * <p>Getter for the field <code>page</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Set the page of the result data (similar to the OFFSET SQL keyword).
	 *
	 * @param page a int.
	 */
	public void setPage(final int page) {
		this.page = page;
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
	 * Sets the page size for pagination  (similar to the LIMIT SQL keyword).
	 *
	 * @param pageSize a int.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * <p>Getter for the field <code>eagerFetchRelationProperties</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getEagerFetchRelationProperties() {
		return eagerFetchRelationProperties;
	}

	/**
	 * <p>isIgnoreCache.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isIgnoreCache() {
		return ignoreCache;
	}

	/**
	 * <p>Setter for the field <code>ignoreCache</code>.</p>
	 *
	 * @param ignoreCache a boolean.
	 */
	public void setIgnoreCache(final boolean ignoreCache) {
		this.ignoreCache = ignoreCache;
	}

	/**
	 * Defines which relation properties should be eagerly fetched. This can be
	 * helpful if those properties are being accessed for sure as it reduces the
	 * amount of database queries at the cost of memory usage.
	 *
	 * @param eagerFetchRelationProperties a {@link java.lang.String} object.
	 */
	public void setEagerFetchRelationProperties(final String... eagerFetchRelationProperties) {
		if (eagerFetchRelationProperties != null) {
			this.eagerFetchRelationProperties.addAll(Arrays.asList(eagerFetchRelationProperties));
		}
	}

	/**
	 * <p>Setter for the field <code>eagerFetchRelationProperties</code>.</p>
	 *
	 * @see #setEagerFetchRelationProperties(String...)
	 * @param eagerFetchRelations a {@link java.util.List} object.
	 */
	public void setEagerFetchRelationProperties(final List<String> eagerFetchRelations) {
		this.eagerFetchRelationProperties.addAll(eagerFetchRelations);
	}

	/**
	 * <p>isEagerFetchRelations.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEagerFetchRelations() {
		return eagerFetchRelations;
	}

	/**
	 * Enable this to eagerly fetch ALL relation properties (item references) in
	 * one query. This overrides the {@link #getEagerFetchRelationProperties()}
	 * property. This can reduce stress on the database, although it increases
	 * memory usage as all data is loaded at once.
	 *
	 * @param eagerFetchRelations a boolean.
	 */
	public void setEagerFetchRelations(final boolean eagerFetchRelations) {
		this.eagerFetchRelations = eagerFetchRelations;
	}

	/**
	 * <p>Getter for the field <code>resultClass</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<T> getResultClass() {
		return resultClass;
	}

//	/**
//	 * <p>Getter for the field <code>limit</code>.</p>
//	 *
//	 * @return a int.
//	 */
//	public int getLimit() {
//		return limit;
//	}
//
//	/**
//	 * Sets the result limit.
//	 *
//	 * @param limit a int.
//	 */
//	public void setLimit(final int limit) {
//		this.limit = limit;
//	}

	/**
	 * @return if true the query results are cached
	 */
	public boolean isCachable() {
		return cachable;
	}

	/**
	 * Specifies if the query results are cached based on the combination of the query parameters.
	 * 
	 * @param cachable
	 */
	public void setCachable(boolean cachable) {
		this.cachable = cachable;
	}

}
