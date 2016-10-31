package at.spot.core.persistence.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.persistence.service.impl.mapdb.DataStorage;

public class MapDBQueryService implements QueryService {

	@Autowired
	protected MapDBService mapDbService;

	@Override
	public QueryResult query(Query query) {
		QueryResult result = null;

		DataStorage storage = mapDbService.getDataStorageForType(query.getType());

		
		return result;
	}

}
