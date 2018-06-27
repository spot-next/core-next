package at.spot.core.persistence.query;

import java.util.HashMap;
import java.util.Map;

import at.spot.core.model.Item;

public class ModelQuery<T extends Item> extends Query<T> {
	private final Map<String, Object> searchParameters = new HashMap<>();

	/**
	 * @param searchParameters
	 *            if empty or null, all items of the given type will be returned.
	 */
	public ModelQuery(final Class<T> resultClass, Map<String, Object> searchParameters) {
		super(resultClass);

		if (searchParameters != null) {
			this.searchParameters.putAll(searchParameters);
		}
	}

	public Map<String, Object> getSearchParameters() {
		return searchParameters;
	}
}
