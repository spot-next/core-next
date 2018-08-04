package at.spot.commerce.service;

import java.util.List;

import at.spot.itemtype.commerce.catalog.Product;
import at.spot.itemtype.commerce.store.BaseStore;
import at.spot.itemtype.commerce.store.FutureStock;
import at.spot.itemtype.commerce.store.Stock;

public interface StockService {

	/**
	 * Returns the stock levels of the given product for the active base store.
	 * 
	 * @param product
	 */
	List<Stock> getStocks(Product product);

	/**
	 * Returns the stock levels of the product with the given id for the active
	 * base store.
	 * 
	 * @param product
	 */
	List<Stock> getStocks(String productId);

	/**
	 * Returns the stock levels of the given product for the given base store.
	 * 
	 * @param product
	 */
	List<Stock> getStocks(Product product, BaseStore baseStore);

	/**
	 * Returns the stock levels of the product with the given id for the given
	 * base store.
	 * 
	 * @param product
	 */
	List<Stock> getStocks(String productId, BaseStore baseStore);

	/**
	 * Returns the future stock levels of the product with the given id for the
	 * given base store.
	 * 
	 * @param product
	 */
	List<FutureStock> getFutureStocks(String productId, BaseStore baseStore);

	/**
	 * Updates the stock of a product
	 * 
	 * @param product
	 * @param baseStore
	 * @param stockAmount
	 * @param reservedAmount
	 */
	void updateStock(Product product, BaseStore baseStore, long stockAmount, long reservedAmount);

	/**
	 * Reduces the amount of stock for the given product.
	 * 
	 * @param product
	 * @param amount
	 */
	void reduceStock(Product product, BaseStore baseStore, long amount);

	/**
	 * Increases the reserved amount of the stock for the given product.
	 * 
	 * @param product
	 * @param baseStore
	 * @param amount
	 */
	void reserveStock(Product product, BaseStore baseStore, long amount);
}
