package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.commerce.checkout.PaymentMethode;
import io.spotnext.itemtype.commerce.checkout.ShippingMethode;
import io.spotnext.itemtype.commerce.order.AbstractOrder;
import io.spotnext.itemtype.core.internationalization.Country;

/**
 * This service provides functionality that revolves around the checkout.
 */
public interface CheckoutService {

	/**
	 * Returns all available shipping methods for the given country, regardless of the destination country.
	 * 
	 * @param currentCountry for which the shipping methods are available in
	 * @return the found shipping methods or an empty list
	 */
	List<ShippingMethode> getSupportedShippingMethods(Country currentCountry);

	/**
	 * Returns all available shipping methods for the given order - based on the current and the destination country.
	 * 
	 * @param order for which the shipping methods are available
	 * @return the found shipping methods or an empty list
	 */
	List<ShippingMethode> getSupportedShippingMethods(AbstractOrder order);

	/**
	 * Returns all available payment methods for the given order.
	 * 
	 * @param order for which the payment methods are available
	 * @return the found payment methods or an empty list
	 */
	List<PaymentMethode> getSupportedPaymentMethods(AbstractOrder order);
}
