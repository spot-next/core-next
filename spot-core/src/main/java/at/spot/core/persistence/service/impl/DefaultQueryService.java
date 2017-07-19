package at.spot.core.persistence.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.persistence.query.QueryCondition;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.QueryException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.QueryService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Service
public class DefaultQueryService extends AbstractService implements QueryService {

	protected static final int MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING = 1000;

	ForkJoinPool threadPool = null;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected ModelService modelService;

	@Override
	public <T extends Item> QueryResult<T> query(final Class<T> type, final QueryCondition<T> query)
			throws QueryException {
		return query(type, query, null, -1, -1, false);
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Override
	public <T extends Item> QueryResult<T> query(final Class<T> type, final QueryCondition<T> query,
			final Comparator<T> orderBy, final int page, final int pageSize, final boolean returnProxies)
			throws QueryException {

		List<T> items = null;

		try {
			ForkJoinTask<List<T>> task = threadPool.submit((Callable<List<T>>) () -> {
				Stream<T> stream = persistenceService.load(type, null, page, pageSize, false,
						MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING, returnProxies);

				if (orderBy != null) {
					stream = stream.sorted(orderBy);
				}

				stream = stream.filter(query);

				return stream.collect(Collectors.toList());
			});

			if (task.isCompletedNormally()) {
				items = task.get();
			} else {
				items = Collections.emptyList();
			}
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() instanceof QueryException) {
				if (e.getCause() instanceof QueryException) {
					throw (QueryException) e.getCause();
				} else {
					throw new QueryException(e.getCause());
				}
			}

			throw new QueryException("Cannot execute search query: " + e.getMessage(), e);
		} catch (final QueryException e) {
			throw e;
		}

		final QueryResult<T> result = new QueryResult<>(items, page, pageSize);

		return result;
	}

	/**
	 * Finds item models using a jexl query that is used as a steam operation. The
	 * single available argument is called "item" of the given type.<br />
	 * Example:<br/>
	 * Query: item.name.size() > 2 and item.group.uid == test-group-1
	 */
	@Override
	public <T extends Item> QueryResult<T> query(final Class<T> type, final String jexlQuery,
			final Comparator<T> orderBy, final int page, final int pageSize, final boolean returnProxies)
			throws QueryException {

		final QueryResult<T> result = query(type, (i) -> {
			return evaluateJexl(jexlQuery, i);
		}, orderBy, page, pageSize, false);

		return result;
	}

	protected <T extends Item> boolean evaluateJexl(final String jexlQuery, final T item) {
		try {
			// Create or retrieve an engine
			final JexlEngine jexl = new JexlBuilder().create();

			// Create an jexl expression
			final String jexlExp = jexlQuery;
			final JexlExpression e = jexl.createExpression(jexlExp);

			// Create a context and add data
			final JexlContext jc = new MapContext();
			jc.set("item", item);

			// Now evaluate the expression, getting the result
			final Object ev = e.evaluate(jc);

			if (ev instanceof Boolean) {
				return (Boolean) ev;
			} else {
				return false;
			}
		} catch (final JexlException e) {
			throw new QueryException(e.getMessage(), e);
		}
	}

	@PostConstruct
	protected void initThreadPool() {
		threadPool = new ForkJoinPool();
	}
}
