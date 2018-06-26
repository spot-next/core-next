package at.spot.core.persistence.query.lambda;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class PredicateTranslationResult {

	private final Set<String> joins = new LinkedHashSet<>();
	private final StringBuilder where = new StringBuilder();
	private final Map<String, Object> parameters = new HashMap<>();

	public Set<String> getJoins() {
		return joins;
	}

	public StringBuilder getWhere() {
		return where;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
}
