package io.spotnext.core.persistence.query;

import java.util.function.Predicate;

import io.spotnext.core.types.Item;

@FunctionalInterface
public interface QueryCondition<T extends Item> extends Predicate<T> {
}
