package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.store.BaseStore;
import io.spotnext.itemtype.commerce.store.FutureStock;
import io.spotnext.itemtype.commerce.store.Stock;

/**
 * <p>StockService interface.</p>
 */
public interface StockService {

	/**
	 * Returns the stock levels of the given product for the active base store.
	 *
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Stock> getStocks(Product product);

	/**
	 * Returns the stock levels of the product with the given id for the active
	 * base store.
	 *
	 * @param productId a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Stock> getStocks(String productId);

	/**
	 * Returns the stock levels of the given product for the given base store.
	 *
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Stock> getStocks(Product product, BaseStore baseStore);

	/**
	 * Returns the stock levels of the product with the given id for the given
	 * base store.
	 *
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 * @param productId a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Stock> getStocks(String productId, BaseStore baseStore);

	/**
	 * Returns the future stock levels of the product with the given id for the
	 * given base store.
	 *
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 * @param productId a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	List<FutureStock> getFutureStocks(String productId, BaseStore baseStore);

	/**
	 * Updates the stock of a product
	 *
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 * @param stockAmount a long.
	 * @param reservedAmount a long.
	 */
	void updateStock(Product product, BaseStore baseStore, long stockAmount, long reservedAmount);

	/**
	 * Reduces the amount of stock for the given product.
	 *
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @param amount a long.
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 */
	void reduceStock(Product product, BaseStore baseStore, long amount);

	/**
	 * Increases the reserved amount of the stock for the given product.
	 *
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @param baseStore a {@link io.spotnext.itemtype.commerce.store.BaseStore} object.
	 * @param amount a long.
	 */
	void reserveStock(Product product, BaseStore baseStore, long amount);
}
