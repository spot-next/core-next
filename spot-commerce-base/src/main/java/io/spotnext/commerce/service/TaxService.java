package io.spotnext.commerce.service;

import java.math.BigDecimal;

import io.spotnext.itemtype.commerce.order.Order;
import io.spotnext.itemtype.commerce.store.Tax;

/**
 * Provides functionality around tax calculation.
 */
public interface TaxService {
	/**
	 * <p>getTaxValue.</p>
	 *
	 * @param order a {@link io.spotnext.itemtype.commerce.order.Order} object.
	 * @return a {@link java.math.BigDecimal} object.
	 */
	BigDecimal getTaxValue(Order order);

	/**
	 * <p>getGrossValue.</p>
	 *
	 * @param price a {@link java.math.BigDecimal} object.
	 * @param tax a {@link io.spotnext.itemtype.commerce.store.Tax} object.
	 * @return a {@link java.math.BigDecimal} object.
	 */
	BigDecimal getGrossValue(BigDecimal price, Tax tax);

	/**
	 * <p>getNetValue.</p>
	 *
	 * @param price a {@link java.math.BigDecimal} object.
	 * @param tax a {@link io.spotnext.itemtype.commerce.store.Tax} object.
	 * @return a {@link java.math.BigDecimal} object.
	 */
	BigDecimal getNetValue(BigDecimal price, Tax tax);
}
