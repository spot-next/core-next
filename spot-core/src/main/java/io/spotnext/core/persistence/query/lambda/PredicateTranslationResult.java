package io.spotnext.core.persistence.query.lambda;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>PredicateTranslationResult class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class PredicateTranslationResult {

	private final Set<String> joins = new LinkedHashSet<>();
	private final StringBuilder where = new StringBuilder();
	private final Map<String, Object> parameters = new HashMap<>();

	/**
	 * <p>Getter for the field <code>joins</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<String> getJoins() {
		return joins;
	}

	/**
	 * <p>Getter for the field <code>where</code>.</p>
	 *
	 * @return a {@link java.lang.StringBuilder} object.
	 */
	public StringBuilder getWhere() {
		return where;
	}

	/**
	 * <p>Getter for the field <code>parameters</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}
}
