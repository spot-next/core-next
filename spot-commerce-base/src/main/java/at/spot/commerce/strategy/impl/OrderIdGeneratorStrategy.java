package at.spot.commerce.strategy.impl;

import org.springframework.beans.factory.annotation.Required;

import at.spot.commerce.model.order.AbstractOrder;
import at.spot.commerce.strategy.SerialIdGeneratorStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Generates order ids with a given pre and suffix.
 */
@SuppressFBWarnings("UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD")
public class OrderIdGeneratorStrategy implements SerialIdGeneratorStrategy<AbstractOrder> {

	protected String prefix;
	protected String suffix;

	@Override
	public String generateId(final AbstractOrder order) {
		final String id = prefix + order.getPk() + suffix;

		return id;
	}

	public String getPrefix() {
		return prefix;
	}

	@Required
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	@Required
	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}
}
