package at.spot.core.persistence.service;

import at.spot.core.persistence.query.JpqlQuery;
import at.spot.core.persistence.query.LambdaQuery;

import at.spot.core.types.Item;

/**
 * Service designed to translate {@link LambdaQuery} to
 * {@link FlexibleSearchQuery}
 */
public interface LambdaQueryTranslationService {

	/**
	 * Translates given query to {@link JpqlQuery}
	 * 
	 * @param query
	 *            to translate
	 * @return flexible search query
	 */
	<T extends Item> JpqlQuery<T> translate(LambdaQuery<T> query);
}
