package at.spot.core.persistence.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.LambdaQuery;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.persistence.hibernate.impl.HibernatePersistenceService;
import at.spot.core.persistence.service.LambdaQueryTranslationService;
import at.spot.core.persistence.service.QueryService;
import at.spot.core.types.Item;

@Service
public class DefaultQueryService extends AbstractService implements QueryService {

	protected static final int MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING = 1000;

	@Resource
	protected HibernatePersistenceService persistenceService;

	@Resource
	protected ModelService modelService;

	@Resource
	protected LambdaQueryTranslationService lambdaQueryTranslationService;

	@Override
	public <T> QueryResult<T> query(String queryString, Class<T> resultClass) {
		final JpqlQuery<T> query = new JpqlQuery<>(queryString, resultClass);

		return query(query);
	}

	@Override
	public <T> QueryResult<T> query(JpqlQuery<T> query) {
		final List<T> resultList = persistenceService.query(query);
		final QueryResult<T> result = new QueryResult<T>(resultList, query.getPage(), query.getPageSize());

		return result;
	}

	@Override
	public <T extends Item> QueryResult<T> query(final LambdaQuery<T> query) {
		// translate lambda query to regular JPGL query
		final JpqlQuery<T> translated = lambdaQueryTranslationService.translate(query);

		final List<T> resultList = query(translated).getResultList();

		return new QueryResult<T>(resultList, query.getPage(), query.getPageSize());
	}
}
