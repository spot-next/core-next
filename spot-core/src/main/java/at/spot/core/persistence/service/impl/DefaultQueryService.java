package at.spot.core.persistence.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;
import at.spot.core.persistence.query.lambda.LambdaQuery;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.hibernate.impl.HibernatePersistenceService;
import at.spot.core.persistence.service.LambdaQueryTranslationService;
import at.spot.core.persistence.service.QueryService;

@Service
public class DefaultQueryService extends AbstractService implements QueryService {

	protected static final int MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING = 1000;

	@Resource
	protected HibernatePersistenceService persistenceService;

	@Resource
	protected ModelService modelService;

	@Resource
	protected LambdaQueryTranslationService lambdaFlexibleSearchTranslationService;

	@Override
	public <T> QueryResult<T> query(String queryString, Class<T> resultClass) {
		final Query<T> query = new Query<>(queryString, resultClass);

		return query(query);
	}

	@Override
	public <T> QueryResult<T> query(Query<T> query) {
		final List<T> resultList = persistenceService.query(query);
		final QueryResult<T> result = new QueryResult<T>(resultList, query.getPage(), query.getPageSize());

		return result;
	}

	@Override
	public <T extends Item> QueryResult<T> query(final LambdaQuery<T> query) {
		final Query<T> translated = lambdaFlexibleSearchTranslationService.translate(query);

		List<T> resultList = query(translated).getResultList();

		return new QueryResult<T>(resultList, query.getPage(), query.getPageSize());
	}

	@Override
	public <T extends Item> T getSingleResult(final LambdaQuery<T> query) {

		query.limit(2);
		final QueryResult<T> resList = query(query);

		if (resList.isEmpty()) {
			throw new ModelNotFoundException("Cannot find item");
		}

		if (resList.count() > 1) {
			throw new ModelNotUniqueException("Found more than 1 result");
		}

		return resList.getResultList().get(0);
	}
}
