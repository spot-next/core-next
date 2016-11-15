package at.spot.core.persistence.service;

import at.spot.core.model.Item;
import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;

public interface QueryService {

	public <T extends Item> QueryResult query(Class<T> type, QueryCondition<T> condition);
}
