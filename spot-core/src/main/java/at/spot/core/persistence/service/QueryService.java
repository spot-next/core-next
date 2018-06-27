package at.spot.core.persistence.service;

import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.LambdaQuery;
import at.spot.core.persistence.query.QueryResult;

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
	<T> QueryResult<T> query(JpqlQuery<T> query);

	/**
	 * Executes lambda query and returns list of results.
	 *
	 * @param query
	 *            the lambda query
	 * @return list of result or empty if nothing is found
	 */
	<T extends Item> QueryResult<T> query(LambdaQuery<T> query);

}
