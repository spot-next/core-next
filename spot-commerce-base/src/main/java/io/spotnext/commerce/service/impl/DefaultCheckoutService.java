package io.spotnext.commerce.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spotnext.commerce.service.CheckoutService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.itemtype.commerce.checkout.PaymentMethode;
import io.spotnext.itemtype.commerce.checkout.ShippingMethode;
import io.spotnext.itemtype.commerce.order.AbstractOrder;
import io.spotnext.itemtype.core.internationalization.Country;

@Service
public class DefaultCheckoutService extends AbstractService implements CheckoutService {

	@Override
	public List<ShippingMethode> getSupportedShippingMethods(Country currentCountry) {
		return Collections.emptyList();
	}

	@Override
	public List<ShippingMethode> getSupportedShippingMethods(AbstractOrder order) {
		return Collections.emptyList();
	}

	@Override
	public List<PaymentMethode> getSupportedPaymentMethods(AbstractOrder order) {
		return Collections.emptyList();
	}

}
