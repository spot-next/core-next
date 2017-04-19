package at.spot.commerce.strategy.impl;

import at.spot.commerce.model.order.AbstractOrder;
import at.spot.commerce.strategy.SerialIdGeneratorStrategy;

public class DefaultSerialIdGeneratorStrategy implements SerialIdGeneratorStrategy<AbstractOrder> {

	protected String prefix;
	protected String suffix;

	@Override
	public String generateId(final AbstractOrder order) {
		final String id = prefix + order.getPk() + suffix;

		return id;
	}

}
