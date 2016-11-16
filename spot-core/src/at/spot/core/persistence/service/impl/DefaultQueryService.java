package at.spot.core.persistence.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.QueryService;

@Service
public class DefaultQueryService extends AbstractService implements QueryService {

	ForkJoinPool threadPool = null;

	@Autowired
	protected PersistenceService persistenceService;

	@Override
	public <T extends Item> QueryResult<T> query(final Class<T> type, final QueryCondition<T> condition,
			final Comparator<T> orderBy, final int page, final int pageSize) throws QueryException {

		List<T> items = null;

		try {
			items = threadPool.submit(() -> {
				Stream<T> stream = persistenceService.load(type, null).parallel();

				if (orderBy != null) {
					stream = stream.sorted(orderBy);
				}

				return stream.filter(condition).collect(Collectors.toList());
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new QueryException("Cannot execute search query.", e);
		}

		final QueryResult<T> result = new QueryResult<>(items, page, pageSize);

		return result;
	}

	@PostConstruct
	protected void initThreadPool() {
		threadPool = new ForkJoinPool();
	}
}
