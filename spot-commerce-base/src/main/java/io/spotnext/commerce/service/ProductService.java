package io.spotnext.commerce.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.itemtype.commerce.catalog.Category;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

/**
 * <p>
 * ProductService interface.
 * </p>
 */
public interface ProductService {

	/**
	 * Returns the product with the given uid and in the active session catalogs.
	 *
	 * @param productId      uid of the product
	 * @param catalogVersion of the product
	 * @return the product that matches the given criteria
	 * @throw {@link ModelNotUniqueException} in case the product appears in multiple session catalogs
	 */
	Optional<Product> getProductForId(String productId) throws ModelNotUniqueException;

	/**
	 * Returns the product with the given uid of the given catalog version.
	 *
	 * @param productId      uid of the product
	 * @param catalogVersion of the product
	 * @return the product that matches the given criteria
	 */
	Optional<Product> getProductForId(String productId, CatalogVersion catalogVersion);

	/**
	 * Returns all products of the given category in the active session catalog.
	 *
	 * @param category a {@link io.spotnext.itemtype.commerce.catalog.Category} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Product> getProductsInCategory(Category category);

	/**
	 * Returns all products of the active session catalog versions.
	 *
	 * @return a non-null list of all products in the session catalog versions.
	 */
	List<Product> getAllProducts();

	/**
	 * Returns all products of the given catalog.
	 *
	 * @param catalogVersions for which all products should be fetched.
	 * @return a non-null list of all products in the given catalogs.
	 */
	List<Product> getAllProducts(Set<CatalogVersion> catalogVersions);

}
