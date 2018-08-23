package io.spotnext.core.persistence.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.hibernate.impl.HibernatePersistenceService;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.LambdaQueryTranslationService;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.core.types.Item;

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
	public <T> QueryResult<T> query(final String queryString, final Class<T> resultClass) {
		final JpqlQuery<T> query = new JpqlQuery<>(queryString, resultClass);

		return query(query);
	}

	@Override
	public <T> QueryResult<T> query(final JpqlQuery<T> query) {
		sanitizeQuery(query);
		final List<T> resultList = persistenceService.query(query);
		final QueryResult<T> result = new QueryResult<T>(resultList, query.getPage(), query.getPageSize());

		// TODO this is a pretty ugly hack, find a way to evict entities in the
		// persistence context, when executing DLM queries.
		// if (query.isIgnoreCache() &&
		// Item.class.isAssignableFrom(query.getResultClass())) {
		// persistenceService.refresh((List<? extends Item>) resultList);
		// }

		return result;
	}

	/**
	 * Removed a trailing ";" that is not allowed for JPQL.
	 * 
	 * @param query the query to sanitize.
	 */
	protected <T> void sanitizeQuery(final JpqlQuery<T> query) {
		String queryStr = StringUtils.remove(query.getQuery().trim(), ";");
		query.setQuery(queryStr);
	}

	@Override
	public <T extends Item> QueryResult<T> query(final LambdaQuery<T> query) {
		// translate lambda query to regular JPGL query
		final JpqlQuery<T> translated = lambdaQueryTranslationService.translate(query);

		final List<T> resultList = query(translated).getResultList();
		return new QueryResult<T>(resultList, query.getPage(), query.getPageSize());
	}
}
