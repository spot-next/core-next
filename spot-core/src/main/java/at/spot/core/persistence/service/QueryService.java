package at.spot.core.persistence.service;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;

public interface QueryService {

	/**
	 * Executes a JPQL query for the given item type. <br />
	 * Example: SELECT u FROM User u WHERE id = 'test'
	 * 
	 * @param query
	 *            the JPQL query
	 * @param the
	 *            type of the result
	 * @return the query result
	 */
	<T> QueryResult<T> query(String query, Class<T> resultClass);

	/**
	 * Executes the given query and returns a query result.
	 * 
	 * @return the query result
	 */
	<T> QueryResult<T> query(Query<T> query);

}
