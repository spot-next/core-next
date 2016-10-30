package at.spot.core.persistence.service.impl;

import at.spot.core.persistence.query.Condition;
import at.spot.core.persistence.query.Operator;
import at.spot.core.persistence.query.Query;
import at.spot.core.persistence.query.QueryResult;

import at.spot.core.model.user.User;
import at.spot.core.persistence.service.QueryService;

public class MapDBQueryService implements QueryService {

	@Override
	public QueryResult query(Query query) {
		QueryResult result = Query.select(User.class)
				.where(Operator.and(Condition.equals("User.groups.uid", "Group.uid")).or(Condition.like("", "")))
				.fetch();

		return result;
	}

}
