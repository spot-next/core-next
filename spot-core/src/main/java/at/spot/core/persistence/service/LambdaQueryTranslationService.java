package at.spot.core.persistence.service;

import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.lambda.LambdaQuery;

import at.spot.core.model.Item;

/**
 * Service designed to translate {@link LambdaQuery} to
 * {@link FlexibleSearchQuery}
 */
public interface LambdaQueryTranslationService {

	/**
	 * Translates given query to {@link Query}
	 * 
	 * @param query
	 *            to translate
	 * @return flexible search query
	 */
	<T extends Item> Query<T> translate(LambdaQuery<T> query);
}
