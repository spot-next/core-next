package io.spotnext.core.persistence.query;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * JpqlQuery class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class JpqlQuery<T> extends Query<T> {
	private String query;
	private final Map<String, Object> params = new HashMap<>();
	private boolean isNativeQuery = false;
	private boolean executeUpdate = false;

	/**
	 * <p>
	 * Constructor for JpqlQuery.
	 * </p>
	 *
	 * @param query a {@link java.lang.String} object.
	 * @param resultClass a {@link java.lang.Class} object.
	 */
	public JpqlQuery(String query, Class<T> resultClass) {
		super(resultClass);
		this.query = query;
	}

	/**
	 * <p>
	 * Constructor for JpqlQuery.
	 * </p>
	 *
	 * @param query the JPQL query string
	 * @param params the query parameters
	 * @param resultClass the mapped type of the results. If this is a JPA entity, it will be mapped directly. If it is a regular POJO, its properties will be
	 * mapped based on the result column names.
	 */
	public JpqlQuery(String query, Map<String, Object> params, Class<T> resultClass) {
		this(query, resultClass);
		this.params.putAll(params);
	}

	/**
	 * <p>
	 * Getter for the field <code>query</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * <p>
	 * Setter for the field <code>query</code>.
	 * </p>
	 *
	 * @param query a {@link java.lang.String} object.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * <p>
	 * Getter for the field <code>params</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * <p>
	 * addParam.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 */
	public void addParam(String name, Object value) {
		this.params.put(name, value);
	}

	/**
	 * <p>
	 * isNativeQuery.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean isNativeQuery() {
		return isNativeQuery;
	}

	/**
	 * <p>
	 * setNativeQuery.
	 * </p>
	 *
	 * @param isNativeQuery a boolean.
	 */
	public void setNativeQuery(boolean isNativeQuery) {
		this.isNativeQuery = isNativeQuery;
	}

	@Override
	public String toString() {
		return query + ", params = " + params.toString();
	}

	/**
	 * If this is set to true, the query will be executed as an update query, possibly altering the database. The return value is an integer number determining
	 * the affected rows.
	 * 
	 * @return the value
	 */
	public boolean isExecuteUpdate() {
		return executeUpdate;
	}

	/**
	 * This has to be set to true to enable INSERT and UPDATE statements.
	 * 
	 * @param executeUpdate
	 */
	public void setExecuteUpdate(boolean executeUpdate) {
		this.executeUpdate = executeUpdate;
	}

}
