package at.spot.core.persistence.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.persistence.service.impl.mapdb.DataStorage;

@Service
public class MapDBQueryService extends AbstractService implements QueryService {

	ForkJoinPool threadPool = null;

	@Autowired
	protected MapDBService mapDbService;

	@Override
	public <T extends Item> QueryResult<T> query(final Class<T> type, final QueryCondition<T> condition) {

		final DataStorage storage = mapDbService.getDataStorageForType(type);

		List<T> items = null;
		try {
			items = threadPool.submit(() -> {
				return mapDbService.load(type, null).parallelStream().filter(condition).collect(Collectors.toList());
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final QueryResult<T> result = new QueryResult<>(items);

		return result;
	}

	@PostConstruct
	protected void initThreadPool() {
		threadPool = new ForkJoinPool(4);
	}
}
