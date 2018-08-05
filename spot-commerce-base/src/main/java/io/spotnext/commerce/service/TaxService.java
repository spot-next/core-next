package io.spotnext.commerce.service;

import java.math.BigDecimal;

import io.spotnext.itemtype.commerce.order.Order;
import io.spotnext.itemtype.commerce.store.Tax;

/**
 * Provides functionality around tax calculation.
 */
public interface TaxService {
	BigDecimal getTaxValue(Order order);

	BigDecimal getGrossValue(BigDecimal price, Tax tax);

	BigDecimal getNetValue(BigDecimal price, Tax tax);
}
