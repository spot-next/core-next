package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.commerce.catalog.Category;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.catalog.Catalog;

/**
 * <p>ProductService interface.</p>
 */
public interface ProductService {

	/**
	 * Returns the product with the given id of the active session catalog.
	 *
	 * @param productId a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 */
	Product getProductForId(String productId);

	/**
	 * Returns the product with the given id of the given catalog.
	 *
	 * @param productId a {@link java.lang.String} object.
	 * @param catalog a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 * @return a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 */
	Product getProductForId(String productId, Catalog catalog);

	/**
	 * Returns all products of the given category in the active session catalog.
	 *
	 * @param category a {@link io.spotnext.itemtype.commerce.catalog.Category} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Product> getProductsInCategory(Category category);

	/**
	 * Returns all products of the active session catalog.
	 *
	 * @return a {@link java.util.List} object.
	 */
	List<Product> getAllProducts();

	/**
	 * Returns all products of the given catalog.
	 *
	 * @param catalog a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Product> getAllProducts(Catalog catalog);

}
