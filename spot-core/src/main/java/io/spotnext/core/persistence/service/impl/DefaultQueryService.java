package io.spotnext.core.persistence.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.exception.QueryException;
import io.spotnext.core.persistence.hibernate.impl.HibernatePersistenceService;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.Queries;
import io.spotnext.core.persistence.query.Query;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.service.LambdaQueryTranslationService;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>
 * DefaultQueryService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultQueryService extends AbstractService implements QueryService {

	protected static final int MIN_ITEM_COUNT_FOR_PARALLEL_PROCESSING = 1000;

	@Autowired
	protected HibernatePersistenceService persistenceService;

	@Autowired
	protected LambdaQueryTranslationService lambdaQueryTranslationService;

	/** {@inheritDoc} */
	@Override
	public <T> QueryResult<T> query(final String queryString, final Class<T> resultClass) {
		final JpqlQuery<T> query = new JpqlQuery<>(queryString, resultClass);

		return query(query);
	}

	/** {@inheritDoc} */
	@Override
	public <T> QueryResult<T> query(final JpqlQuery<T> query) {
		sanitizeQuery(query);
		checkConsistency(query);

//		final List<T> resultList = persistenceService.query(query);

		// if resultList is null this means that we actually didn't query anything, e.g when using an UPDATE or INSERT query
//		final long totalCount = getTotalCount(query, resultList != null ? resultList.size() : 0);

//		final QueryResult<T> result = new QueryResult<T>(resultList, query.getPage(), query.getPageSize(),
//				totalCount);

		// TODO this is a pretty ugly hack, find a way to evict entities in the
		// persistence context, when executing DLM queries.
		// if (query.isIgnoreCache() &&
		// Item.class.isAssignableFrom(query.getResultClass())) {
		// persistenceService.refresh((List<? extends Item>) resultList);
		// }

		return persistenceService.query(query);
	}

	private <T> void checkConsistency(JpqlQuery<T> query) throws QueryException {
		String queryStr = query.getQuery();
		
		if (!query.isExecuteUpdate() && (queryStr.startsWith("UPDATE") || queryStr.startsWith("INSERT") || queryStr.startsWith("DELETE"))) {
			throw new QueryException(String.format("Query '%s' is not allowed to execute updates.", queryStr));
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> QueryResult<T> query(final LambdaQuery<T> query) {
		// translate lambda query to regular JPGL query
		final JpqlQuery<T> translated = lambdaQueryTranslationService.translate(query);

		return query(translated);
	}

//	private <T> long getTotalCount(JpqlQuery<T> originalQuery, int resultCount) {
//		if (Item.class.isAssignableFrom(originalQuery.getResultClass()) && originalQuery.getPageSize() > 0) {
//			totalCount = persistenceService.query(Queries.countAll(originalQuery.getResultClass())).get(0);
//		} else {
//			totalCount = resultCount;
//		}

//		final String countQueryString = "SELECT count(c) FROM (" + originalQuery.getQuery() + ") c";
//		final JpqlQuery<Long> countQuery = new JpqlQuery<>(countQueryString, Long.class);
//		countQuery.getParams().putAll(originalQuery.getParams());
//		countQuery.setNativeQuery(true);

//		List<Long> result = persistenceService.query(countQuery);

//		return result.size() > 0 ? result.get(0) : null;
//	}

	/**
	 * Removed a trailing ";" that is not allowed for JPQL.
	 * 
	 * @param query the query to sanitize.
	 */
	protected <T> void sanitizeQuery(final JpqlQuery<T> query) {
		String queryStr = StringUtils.remove(query.getQuery().trim(), ";");
		query.setQuery(queryStr);
	}

//	private static String queryToSqlString(String query) {
//        Session session = em.unwrap(JpaEntityManager.class).getActiveSession();
//        DatabaseQuery databaseQuery = query.unwrap(EJBQueryImpl.class).getDatabaseQuery();
//        databaseQuery.prepareCall(session, new DatabaseRecord());
//        Record r = databaseQuery.getTranslationRow();

	// Query with parameters
//        return databaseQuery.getTranslatedSQLString(session, r);
//    }

//    /**
//     * Overload, see {@link #queryToSqlString(EntityManager, Query)}
//     */
//    private static String queryToSqlString(EntityManager em, String statement) {
//        return queryToSqlString(em, em.createQuery(statement));
//    }
}
