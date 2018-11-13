package io.spotnext.core.persistence.service.impl;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.trigersoft.jaque.expression.LambdaExpression;

import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.lambda.ParametersNameGenerator;
import io.spotnext.core.persistence.query.lambda.PredicateTranslationResult;
import io.spotnext.core.persistence.query.lambda.SerializablePredicate;
import io.spotnext.core.persistence.query.lambda.ToJpqlQueryVisitor;
import io.spotnext.core.persistence.service.LambdaQueryTranslationService;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>DefaultLambdaQueryTranslationService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultLambdaQueryTranslationService implements LambdaQueryTranslationService {

	/** Constant <code>FS_MAIN_ALIAS="item"</code> */
	public static final String FS_MAIN_ALIAS = "item";

	@Resource
	private ModelService modelService;

	/** {@inheritDoc} */
	@Override
	public <T extends Item> JpqlQuery<T> translate(final LambdaQuery<T> query) {

		final List<SerializablePredicate<T>> filters = query.getFilters();

		final ParametersNameGenerator generator = new ParametersNameGenerator();
		final PredicateTranslationResult allFiltersResult = new PredicateTranslationResult();

		for (final Predicate<T> filter : filters) {
			final PredicateTranslationResult singleResult = LambdaExpression.parse(filter)
					.accept(new ToJpqlQueryVisitor(generator, modelService));

			allFiltersResult.getJoins().addAll(singleResult.getJoins());
			allFiltersResult.getParameters().putAll(singleResult.getParameters());
			final StringBuilder allWhere = allFiltersResult.getWhere();
			final String filterWhere = singleResult.getWhere().toString().trim();
			if (!filterWhere.isEmpty()) {
				if (allWhere.length() > 0) {
					allWhere.append(" AND ");
				}
				allWhere.append('(').append(filterWhere).append(')');
			}
		}

		final JpqlQuery<T> jpqlQuery = createQuery(allFiltersResult, query.getResultClass());
//		if (query.getLimit() > 0) {
//			jpqlQuery.setLimit(query.getLimit());
//		}
		
		jpqlQuery.setCachable(query.isCachable());
		jpqlQuery.setIgnoreCache(query.isIgnoreCache());
		jpqlQuery.setEagerFetchRelationProperties(query.getEagerFetchRelationProperties());
		jpqlQuery.setPage(query.getPage());
		jpqlQuery.setPageSize(query.getPageSize());
		jpqlQuery.setClearCaches(query.isClearCaches());
		
		return jpqlQuery;
	}

	private <T> JpqlQuery<T> createQuery(final PredicateTranslationResult allFiltersResult, final Class<T> itemClass) {

		final String typeCode = itemClass.getSimpleName();
		final StringBuilder query = new StringBuilder("SELECT ").append(FS_MAIN_ALIAS).append(" FROM ").append(typeCode)
				.append(" AS ").append(FS_MAIN_ALIAS);
		final String joins = String.join(" ", allFiltersResult.getJoins());
		if (!joins.isEmpty()) {
			query.append(" ").append(joins);
		}

		final StringBuilder where = allFiltersResult.getWhere();
		if (where.length() > 0) {
			query.append(" WHERE ").append(where);
		}

		return new JpqlQuery<T>(query.toString(), allFiltersResult.getParameters(), itemClass);
	}
}
