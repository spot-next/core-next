package at.spot.commerce.service;

import java.math.BigDecimal;

import at.spot.commerce.model.order.Order;
import at.spot.commerce.model.store.Tax;

/**
 * Provides functionality around tax calculation.
 */
public interface TaxService {
	BigDecimal getTaxValue(Order order);

	BigDecimal getGrossValue(BigDecimal price, Tax tax);

	BigDecimal getNetValue(BigDecimal price, Tax tax);
}
