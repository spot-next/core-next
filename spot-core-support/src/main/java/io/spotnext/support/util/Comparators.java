package io.spotnext.support.util;

import java.util.Comparator;

public class Comparators {
	public static <T> Comparator<T> propertyBasedComparator(String propertyName, boolean ascending) {
		Comparator<T> comparator = new Comparator<>() {
			public int compare(T o1, T o2) {
				Object fieldValue1 = ClassUtil.getField(o1, propertyName, true);
				Object fieldValue2 = ClassUtil.getField(o2, propertyName, true);

				if (fieldValue1 instanceof Comparable && fieldValue2 instanceof Comparable) {
					return (ascending ? 1 : -1) * ((Comparable) fieldValue1).compareTo(fieldValue2);
				}

				return 0;
			};
		};

		return comparator;
	}
}
