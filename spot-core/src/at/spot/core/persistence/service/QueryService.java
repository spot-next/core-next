package at.spot.core.persistence.service;

import java.util.Comparator;

import at.spot.core.model.Item;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;

public interface QueryService {

	<T extends Item> QueryResult<T> query(Class<T> type, QueryCondition<T> query) throws QueryException;

	<T extends Item> QueryResult<T> query(Class<T> type, QueryCondition<T> query, Comparator<T> orderBy, int page,
			int pageSize, boolean returnProxies) throws QueryException;

	<T extends Item> QueryResult<T> query(Class<T> type, String jexlQuery, Comparator<T> orderBy, final int page,
			final int pageSize, boolean returnProxies) throws QueryException;
}
