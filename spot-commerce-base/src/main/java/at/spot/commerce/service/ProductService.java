package at.spot.commerce.service;

import java.util.List;

import at.spot.commerce.model.catalog.Category;
import at.spot.commerce.model.catalog.Product;
import at.spot.core.model.catalog.Catalog;

public interface ProductService {

	/**
	 * Returns the product with the given id of the active session catalog.
	 * 
	 * @param productId
	 * @param catalog
	 * @return
	 */
	Product getProductForId(String productId);

	/**
	 * Returns the product with the given id of the given catalog.
	 * 
	 * @param productId
	 * @param catalog
	 * @return
	 */
	Product getProductForId(String productId, Catalog catalog);

	/**
	 * Returns all products of the given category in the active session catalog.
	 * 
	 * @param catalog
	 * @return
	 */
	List<Product> getProductsInCategory(Category category);

	/**
	 * Returns all products of the active session catalog.
	 * 
	 * @return
	 */
	List<Product> getAllProducts();

	/**
	 * Returns all products of the given catalog.
	 * 
	 * @param catalog
	 * @return
	 */
	List<Product> getAllProducts(Catalog catalog);

}
