package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.commerce.catalog.Category;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.catalog.Catalog;

public interface ProductService {

	/**
	 * Returns the product with the given id of the active session catalog.
	 * 
	 * @param productId
	 */
	Product getProductForId(String productId);

	/**
	 * Returns the product with the given id of the given catalog.
	 * 
	 * @param productId
	 * @param catalog
	 */
	Product getProductForId(String productId, Catalog catalog);

	/**
	 * Returns all products of the given category in the active session catalog.
	 * 
	 * @param catalog
	 */
	List<Product> getProductsInCategory(Category category);

	/**
	 * Returns all products of the active session catalog.
	 * 
	 */
	List<Product> getAllProducts();

	/**
	 * Returns all products of the given catalog.
	 * 
	 * @param catalog
	 */
	List<Product> getAllProducts(Catalog catalog);

}
