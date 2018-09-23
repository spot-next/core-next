package io.spotnext.core.persistence.query;

import java.util.function.Predicate;

import io.spotnext.infrastructure.type.Item;

/**
 * <p>QueryCondition interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@FunctionalInterface
public interface QueryCondition<T extends Item> extends Predicate<T> {
}
