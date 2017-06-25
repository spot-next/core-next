package at.spot.core.persistence.query;

import at.spot.core.model.Item;

public class Query {
	protected Class<? extends Item> type;
	protected Condition condition;

	public static Select select(Class<? extends Item> type) {
		Query query = new Query();
		query.setType(type);

		return new Select(query);
	}

	public Class<? extends Item> getType() {
		return type;
	}

	void setType(Class<? extends Item> type) {
		this.type = type;
	}

	public Condition getCondition() {
		return condition;
	}

	void setCondition(Condition condition) {
		this.condition = condition;
	}

}
