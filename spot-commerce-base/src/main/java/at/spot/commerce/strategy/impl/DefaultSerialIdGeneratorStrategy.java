package at.spot.commerce.strategy.impl;

import java.util.UUID;

import at.spot.commerce.strategy.SerialIdGeneratorStrategy;
import at.spot.core.model.Item;

/**
 * Generates a random UID using {@link UUID#randomUUID()}.
 */
public class DefaultSerialIdGeneratorStrategy implements SerialIdGeneratorStrategy<Item> {

    @Override
    public String generateId(Item item) {
        return UUID.randomUUID().toString();
    }
}
