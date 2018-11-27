package io.spotnext.core.persistence.query;

public class Queries {
	public static <T> JpqlQuery<T> selectAll(Class<T> resultType) {
		return new JpqlQuery<>("SELECT x FROM " + resultType.getSimpleName() + " x", resultType);
	}

	public static <T> JpqlQuery<Long> countAll(Class<T> resultType) {
		return new JpqlQuery<Long>("SELECT count(x.id) FROM " + resultType.getSimpleName() + " x", Long.class);
	}
}
