package at.spot.core.persistence.service;

import at.spot.core.persistence.query.QueryResult;

import at.spot.core.model.Item;

public interface QueryService {

	// <T extends Item> QueryResult<T> query(Class<T> type, QueryCondition<T> query)
	// throws QueryException;
	//
	// <T extends Item> QueryResult<T> query(Class<T> type, QueryCondition<T> query,
	// Comparator<T> orderBy, int page,
	// int pageSize) throws QueryException;
	//
	// <T extends Item> QueryResult<T> query(Class<T> type, String jexlQuery,
	// Comparator<T> orderBy, final int page,
	// final int pageSize) throws QueryException;

	<T extends Item> QueryResult<T> query(String query, Class<T> resultClass);

	<T extends Item> QueryResult<T> query(String query, Class<T> resultClass, final int page, final int pageSize);

	// <T extends Item> Query<T> createQuery();
}
