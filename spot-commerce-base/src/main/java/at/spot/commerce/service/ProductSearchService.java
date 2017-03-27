package at.spot.commerce.service;

import java.util.List;
import java.util.Map;

import at.spot.commerce.model.catalog.Product;

/**
 * Provides functionality
 */
public interface ProductSearchService {

	/**
	 * Searches for products containing the given string. How the search term in
	 * is interpreted and what product properties are searched depends on the
	 * underlying implementation.
	 * 
	 * @param freeTextSearchTerm
	 * @return
	 */
	List<Product> search(String freeTextSearchTerm);

	/**
	 * Searches for products with the given properties.
	 * 
	 * @param freeTextSearchTerms
	 * @return
	 */
	List<Product> search(Map<String, String> freeTextSearchTerms);
}
