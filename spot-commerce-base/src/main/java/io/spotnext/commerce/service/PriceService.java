package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.commerce.catalog.Price;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

/**
 * <p>PriceService interface.</p>
 */
public interface PriceService {

	/**
	 * Returns the prices for the given product.<br>
	 *
	 * @param product a {@link io.spotnext.itemtype.commerce.catalog.Product} object.
	 * @param user
	 * @param userGroup
	 * @param userGroup a {@link io.spotnext.itemtype.core.user.UserGroup} object.
	 * @return a {@link java.util.List} object.
	 */
	List<Price> getPrices(Product product, User user, UserGroup userGroup);
}
