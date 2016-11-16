package at.spot.core.persistence.service;

import java.util.Comparator;

import at.spot.core.model.Item;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;

public interface QueryService {

	public <T extends Item> QueryResult<T> query(Class<T> type, QueryCondition<T> condition,
			final Comparator<T> orderBy, int page, int pageSize) throws QueryException;
}
