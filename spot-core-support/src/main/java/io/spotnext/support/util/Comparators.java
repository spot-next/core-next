package io.spotnext.support.util;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Comparators {
	private static final Map<String, Comparator> CACHE = new ConcurrentHashMap<>();

	public static <T> Comparator<T> propertyBasedComparator(String propertyName, boolean ascending) {
		final String cacheKey = createCacheKey(propertyName, ascending);
		Comparator<T> comparator = CACHE.get(cacheKey);

		if (comparator == null) {
			comparator = new Comparator<>() {
				public int compare(T o1, T o2) {
					Object fieldValue1 = ClassUtil.getField(o1, propertyName, true);
					Object fieldValue2 = ClassUtil.getField(o2, propertyName, true);

					if (fieldValue1 instanceof Comparable && fieldValue2 instanceof Comparable) {
						return (ascending ? 1 : -1) * ((Comparable) fieldValue1).compareTo(fieldValue2);
					}

					return 0;
				};
			};

			CACHE.put(cacheKey, comparator);
		}

		return comparator;
	}

	private static String createCacheKey(String propertyName, boolean ascending) {
		return propertyName + "-" + (ascending ? "ASC" : "DESC");
	}
}
