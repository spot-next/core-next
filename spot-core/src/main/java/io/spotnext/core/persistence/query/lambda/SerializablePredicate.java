package io.spotnext.core.persistence.query.lambda;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * <p>SerializablePredicate interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SerializablePredicate<T> extends Predicate<T>, Serializable {
}
