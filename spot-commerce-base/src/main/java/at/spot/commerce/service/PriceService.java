package at.spot.commerce.service;

import java.util.List;

import at.spot.commerce.model.catalog.Price;
import at.spot.commerce.model.catalog.Product;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;

public interface PriceService {

	/**
	 * Returns the prices for the given product.<br />
	 * 
	 * @param product
	 * @param user
	 * @param userGroup
	 * @return
	 */
	List<Price> getPrices(Product product, User user, UserGroup userGroup);
}
