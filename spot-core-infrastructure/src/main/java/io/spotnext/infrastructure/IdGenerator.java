package io.spotnext.infrastructure;

import io.spotnext.infrastructure.type.Item;

public interface IdGenerator {

	String createStringId(Class<? extends Item> itemType) throws SequenceAccessException;

	Long createLongId(Class<? extends Item> itemType) throws SequenceAccessException;
}
