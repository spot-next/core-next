package at.spot.commerce.service;

import java.math.BigDecimal;

import at.spot.itemtype.commerce.order.Order;
import at.spot.itemtype.commerce.store.Tax;

/**
 * Provides functionality around tax calculation.
 */
public interface TaxService {
	BigDecimal getTaxValue(Order order);

	BigDecimal getGrossValue(BigDecimal price, Tax tax);

	BigDecimal getNetValue(BigDecimal price, Tax tax);
}
