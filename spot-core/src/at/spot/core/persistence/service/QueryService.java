package at.spot.core.persistence.service;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;

public interface QueryService {

	public QueryResult query(Query query);
}
