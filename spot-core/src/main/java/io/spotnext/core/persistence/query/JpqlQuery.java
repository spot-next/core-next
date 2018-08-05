package io.spotnext.core.persistence.query;

import java.util.HashMap;
import java.util.Map;

public class JpqlQuery<T> extends Query<T> {
	private String query;
	private final Map<String, Object> params = new HashMap<>();
	private boolean isNativeQuery = false;

	public JpqlQuery(String query, Class<T> resultClass) {
		super(resultClass);
		this.query = query;
	}

	/**
	 * @param query
	 *            the JPQL query string
	 * @param params
	 *            the query parameters
	 * @param resultClass
	 *            the mapped type of the results. If this is a JPA entity, it will
	 *            be mapped directly. If it is a regular POJO, its properties will
	 *            be mapped based on the result column names.
	 */
	public JpqlQuery(String query, Map<String, Object> params, Class<T> resultClass) {
		this(query, resultClass);
		this.params.putAll(params);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void addParam(String name, Object value) {
		this.params.put(name, value);
	}

	public boolean isNativeQuery() {
		return isNativeQuery;
	}

	public void setNativeQuery(boolean isNativeQuery) {
		this.isNativeQuery = isNativeQuery;
	}

}
