package io.spotnext.core.persistence.query;

import org.apache.commons.lang3.StringUtils;

public class Queries {
	public static <T> JpqlQuery<T> selectAll(final Class<T> resultType) {
		return selectAll(resultType, null);
	}

	public static <T> JpqlQuery<T> selectAll(final Class<T> resultType, final String orderBy) {
		return new JpqlQuery<>("SELECT x FROM " + resultType.getSimpleName() + " x " + (StringUtils.isNotBlank(orderBy) ? "ORDER BY " + orderBy : ""),
				resultType);
	}

	public static <T> JpqlQuery<Long> countAll(final Class<T> resultType) {
		return new JpqlQuery<Long>("SELECT count(x.id) FROM " + resultType.getSimpleName() + " x", Long.class);
	}
}
