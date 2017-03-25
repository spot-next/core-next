package at.spot.commerce.service;

import at.spot.commerce.model.order.AbstractOrder;

public interface CalculationService {

	void calculate(AbstractOrder arder);
}
