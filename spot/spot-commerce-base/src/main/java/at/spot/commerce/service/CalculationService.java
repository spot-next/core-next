package at.spot.commerce.service;

import java.math.BigDecimal;

import at.spot.commerce.model.catalog.Price;
import at.spot.commerce.model.order.AbstractOrder;

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
