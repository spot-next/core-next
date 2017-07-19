package at.spot.core.persistence.query;

public class Where implements QueryPart {
	protected Query query;

	Where(Query query) {
		this.query = query;
	}

	public Query build() {
		return query;
	}
}
