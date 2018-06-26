package at.spot.core.persistence.service;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.query.lambda.LambdaQuery;

import at.spot.core.model.Item;

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

	/**
	 * Executes given query and returns list of results
	 *
	 * @param query
	 *            the query
	 * @return list of result or empty if not found
	 */
	<T extends Item> QueryResult<T> query(LambdaQuery<T> query);

	/**
	 * Executes given query and returns single result
	 *
	 * @param query
	 *            the query
	 * @return single result returned by query (never null)
	 * @throws UnknownIdentifierException
	 *             if query did not return any result
	 * @throws AmbiguousIdentifierException
	 *             if query returned more than 1 result
	 */
	<T extends Item> T getSingleResult(LambdaQuery<T> query);
}
