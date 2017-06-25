package at.spot.commerce.strategy.impl;

import at.spot.commerce.model.order.AbstractOrder;
import at.spot.commerce.strategy.SerialIdGeneratorStrategy;
import org.springframework.beans.factory.annotation.Required;

/**
 * Geberates order ids with a given pre and suffix.
 */
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
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    @Required
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
