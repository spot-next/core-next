package at.spot.commerce.service;

import at.spot.commerce.model.catalog.Product;
import at.spot.commerce.model.store.Stock;

public interface StockService {

	Stock getStock(Product product);

	Stock getStock(String productId);
}
