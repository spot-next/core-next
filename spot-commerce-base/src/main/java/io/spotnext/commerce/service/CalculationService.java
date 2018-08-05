package io.spotnext.commerce.service;

import java.math.BigDecimal;

import io.spotnext.itemtype.commerce.catalog.Price;
import io.spotnext.itemtype.commerce.order.AbstractOrder;

/**
 * The {@link CalculationService} provides functionality to calculate carts,
 * orders, prices, taxes and the like.
 */
public interface CalculationService {

	/**
	 * Calculates the {@link AbstractOrder} object, including total prices,
	 * delivery costs and taxes.
	 * 
	 * @param arder
	 */
	void calculate(AbstractOrder arder);

	BigDecimal calculateTotalPrice(Price price, int quantity);
}
