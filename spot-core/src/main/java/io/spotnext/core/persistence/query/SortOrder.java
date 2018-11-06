package io.spotnext.core.persistence.query;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class SortOrder {
	private String columnName;
	private OrderDirection direction;

	private SortOrder(String columnName, OrderDirection direction) {
		this.columnName = columnName;
		this.direction = direction;
	}

	public static SortOrder of(String sqlOrderByClause) {
		if (StringUtils.isNotBlank(sqlOrderByClause)) {
			String[] split = StringUtils.split(sqlOrderByClause.trim(), " ");

			for (byte b = 0; b < split.length; b++) {
				split[b] = split[b].trim();
			}

			return new SortOrder(split[0], split.length == 2 ? OrderDirection.valueOf(split[1].toUpperCase(Locale.getDefault())) : OrderDirection.ASC);
		}

		throw new IllegalArgumentException("Cannot parse orderBy clause.");
	}

	public static SortOrder of(String columnName, OrderDirection direction) {
		return new SortOrder(columnName, direction);
	}

	public String getColumnName() {
		return columnName;
	}

	public OrderDirection getDirection() {
		return direction;
	}

	public static enum OrderDirection {
		ASC, DESC
	}
}