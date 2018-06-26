package at.spot.core.persistence.service.impl;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.trigersoft.jaque.expression.LambdaExpression;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.lambda.LambdaQuery;
import at.spot.core.persistence.query.lambda.ParametersNameGenerator;
import at.spot.core.persistence.query.lambda.PredicateTranslationResult;
import at.spot.core.persistence.query.lambda.SerializablePredicate;
import at.spot.core.persistence.query.lambda.ToFlexibleSearchVisitor;

import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.Item;
import at.spot.core.persistence.service.LambdaQueryTranslationService;

@Service
public class DefaultLambdaQueryTranslationService implements LambdaQueryTranslationService {

	public static final String FS_MAIN_ALIAS = "this";

	@Resource
	private ModelService modelService;

	@Override
	public <T extends Item> Query<T> translate(final LambdaQuery<T> query) {

		final List<SerializablePredicate<T>> filters = query.getFilters();

		final ParametersNameGenerator generator = new ParametersNameGenerator();
		final PredicateTranslationResult allFiltersResult = new PredicateTranslationResult();

		for (final Predicate<T> filter : filters) {

			final PredicateTranslationResult singleResult = LambdaExpression.parse(filter)
					.accept(new ToFlexibleSearchVisitor(generator, modelService));

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

		final Query<T> result = createQuery(allFiltersResult, query.getItemClass());
		// if (query.getLimit() > 0) {
		// result.setCount(query.getLimit());
		// }
		return result;
	}

	private <T> Query<T> createQuery(final PredicateTranslationResult allFiltersResult, final Class<T> itemClass) {

		final String typeCode = itemClass.getSimpleName();
		final StringBuilder query = new StringBuilder("SELECT ").append(FS_MAIN_ALIAS).append(".PK FROM ")
				.append(typeCode).append(" AS ").append(FS_MAIN_ALIAS);
		final String joins = String.join(" ", allFiltersResult.getJoins());
		if (!joins.isEmpty()) {
			query.append(" ").append(joins);
		}

		final StringBuilder where = allFiltersResult.getWhere();
		if (where.length() > 0) {
			query.append(" WHERE ").append(where);
		}

		return new Query<T>(query.toString(), allFiltersResult.getParameters(), itemClass);

	}
}
