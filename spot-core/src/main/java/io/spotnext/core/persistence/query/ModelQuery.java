package io.spotnext.core.persistence.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.spotnext.infrastructure.type.Item;

/**
 * <p>
 * ModelQuery class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ModelQuery<T extends Item> extends Query<T> {
	private final Map<String, Object> searchParameters = new HashMap<>();

	private final List<SortOrder> orderBy = new ArrayList<>();

	/**
	 * <p>
	 * Constructor for ModelQuery.
	 * </p>
	 *
	 * @param searchParameters if empty or null, all items of the given type will be returned.
	 * @param resultClass      a {@link java.lang.Class} object.
	 */
	public ModelQuery(final Class<T> resultClass, Map<String, Object> searchParameters) {
		super(resultClass);

		if (searchParameters != null) {
			this.searchParameters.putAll(searchParameters);
		}
	}

	/**
	 * <p>
	 * Getter for the field <code>searchParameters</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, Object> getSearchParameters() {
		return searchParameters;
	}

	/**
	 * Adds {@link SortOrder} objects to the orderBy clause. If null is passed, nothing is added.
	 * 
	 * @param orderBys
	 */
	public void addOrderBy(SortOrder... orderBys) {
		if (orderBys != null) {
			orderBy.addAll(Arrays.asList(orderBys));
		}
	}

	/**
	 * @return the orderBy definitions
	 */
	public List<SortOrder> getOrderBy() {
		return orderBy;
	}

}
