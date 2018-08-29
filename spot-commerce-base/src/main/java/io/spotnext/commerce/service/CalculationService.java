package io.spotnext.commerce.service;

import java.math.BigDecimal;

import io.spotnext.itemtype.commerce.catalog.Price;
import io.spotnext.itemtype.commerce.order.AbstractOrder;

/**
 * The {@link io.spotnext.commerce.service.CalculationService} provides functionality to calculate carts,
 * orders, prices, taxes and the like.
 */
public interface CalculationService {

	/**
	 * Calculates the {@link io.spotnext.itemtype.commerce.order.AbstractOrder} object, including total prices,
	 * delivery costs and taxes.
	 *
	 * @param arder a {@link io.spotnext.itemtype.commerce.order.AbstractOrder} object.
	 */
	void calculate(AbstractOrder arder);

	/**
	 * <p>calculateTotalPrice.</p>
	 *
	 * @param price a {@link io.spotnext.itemtype.commerce.catalog.Price} object.
	 * @param quantity a int.
	 * @return a {@link java.math.BigDecimal} object.
	 */
	BigDecimal calculateTotalPrice(Price price, int quantity);
}
