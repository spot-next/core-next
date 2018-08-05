package io.spotnext.core.persistence.service;

import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.types.Item;

/**
 * Service designed to translate {@link LambdaQuery} to a JPQL query.
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
