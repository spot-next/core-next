package at.spot.core.persistence.query;

import at.spot.core.model.Item;

public class Select implements QueryPart {
	protected Query query;

	Select(Query query) {
		this.query = query;
	}

	public <T extends Item> Where where(Condition condition) {
		query.setCondition(condition);
		
		return new Where(query);
	}

	public Query build() {
		return query;
	}
}
