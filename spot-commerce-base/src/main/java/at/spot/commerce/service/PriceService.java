package at.spot.commerce.service;

import java.util.List;

import at.spot.itemtype.commerce.catalog.Price;
import at.spot.itemtype.commerce.catalog.Product;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

public interface PriceService {

	/**
	 * Returns the prices for the given product.<br>
	 * 
	 * @param product
	 * @param user
	 * @param userGroup
	 */
	List<Price> getPrices(Product product, User user, UserGroup userGroup);
}
