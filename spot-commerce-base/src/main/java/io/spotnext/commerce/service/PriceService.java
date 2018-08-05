package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.commerce.catalog.Price;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

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
