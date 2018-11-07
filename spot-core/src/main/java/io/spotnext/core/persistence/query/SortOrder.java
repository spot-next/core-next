package io.spotnext.core.persistence.query;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.core.infrastructure.support.Logger;

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
				split[b] = StringUtils.trimToEmpty(split[b]);
			}

			OrderDirection dir = OrderDirection.ASC;
			String dirString = "";

			if (split.length == 2) {
				dirString = split[1].toUpperCase(Locale.getDefault());

				try {
					dir = OrderDirection.valueOf(dirString);
				} catch (IllegalArgumentException e) {
					Logger.warn(String.format("Unknown order direction '%s'", dirString));
				}
			}

			return new SortOrder(split[0], dir);
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