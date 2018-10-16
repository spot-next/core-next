package io.spotnext.core.persistence.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spotnext.core.persistence.query.lambda.SerializablePredicate;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>LambdaQuery class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class LambdaQuery<T extends Item> extends Query<T> {

	private final List<SerializablePredicate<T>> filters = new ArrayList<>();

	/**
	 * <p>Constructor for LambdaQuery.</p>
	 *
	 * @param resultClass a {@link java.lang.Class} object.
	 */
	public LambdaQuery(final Class<T> resultClass) {
		super(resultClass);
	}

	/**
	 * <p>filter.</p>
	 *
	 * @param filter a {@link io.spotnext.core.persistence.query.lambda.SerializablePredicate} object.
	 * @return a {@link io.spotnext.core.persistence.query.LambdaQuery} object.
	 */
	public LambdaQuery<T> filter(final SerializablePredicate<T> filter) {
		filters.add(filter);
		return this;
	}

//	/**
//	 * <p>limit.</p>
//	 *
//	 * @param limit a int.
//	 * @return a {@link io.spotnext.core.persistence.query.LambdaQuery} object.
//	 */
//	public LambdaQuery<T> limit(final int limit) {
//		this.limit = limit;
//		return this;
//	}

	/**
	 * <p>Getter for the field <code>filters</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<SerializablePredicate<T>> getFilters() {
		return Collections.unmodifiableList(filters);
	}

}
