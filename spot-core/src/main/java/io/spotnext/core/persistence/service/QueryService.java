package io.spotnext.core.persistence.service;

import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>QueryService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface QueryService {

	/**
	 * Executes a JPQL query for the given item type. <br />
	 * Example: SELECT u FROM User u WHERE id = 'test'
	 *
	 * @param query
	 *            the JPQL query
	 * @param resultClass
	 *            the type of the result
	 * @return the query result
	 * @param <T> a T object.
	 */
	<T> QueryResult<T> query(String query, Class<T> resultClass);

	/**
	 * Executes the given query and returns a query result.
	 *
	 * @return the query result
	 * @param query a {@link io.spotnext.core.persistence.query.JpqlQuery} object.
	 * @param <T> a T object.
	 */
	<T> QueryResult<T> query(JpqlQuery<T> query);

	/**
	 * Executes lambda query and returns list of results.
	 *
	 * @param query
	 *            the lambda query
	 * @return list of result or empty if nothing is found
	 * @param <T> a T object.
	 */
	<T extends Item> QueryResult<T> query(LambdaQuery<T> query);

}
