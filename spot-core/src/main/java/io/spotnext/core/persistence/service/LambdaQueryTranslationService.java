package io.spotnext.core.persistence.service;

import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.infrastructure.type.Item;

/**
 * Service designed to translate {@link io.spotnext.core.persistence.query.LambdaQuery} to a JPQL query.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface LambdaQueryTranslationService {

	/**
	 * Translates given query to {@link io.spotnext.core.persistence.query.JpqlQuery}
	 *
	 * @param query
	 *            to translate
	 * @return flexible search query
	 * @param <T> a T object.
	 */
	<T extends Item> JpqlQuery<T> translate(LambdaQuery<T> query);
}
