package at.spot.core.support.util;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unchecked")
public class MiscUtil {

	/**
	 * Calls close() on all given objects that implement the {@link Closeable}
	 * interface. Doesn't throw any exceptions at all.
	 * 
	 * @param closableObject
	 */
	public static void closeQuietly(final Closeable... closableObjects) {
		try {
			for (final Closeable c : closableObjects) {
				if (c != null) {
					c.close();
				}
			}
		} catch (final Exception e) {
			// ignore exceptions
		}
	}

	public static int intOrDefault(final String value, final int defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Integer.parseInt(value);
		}

		return defaultValue;
	}

	public static long longOrDefault(final String value, final long defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Long.parseLong(value);
		}

		return defaultValue;
	}

	public static double doubleOrDefault(final String value, final double defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Double.parseDouble(value);
		}

		return defaultValue;
	}

	public static float floatOrDefault(final String value, final float defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Float.parseFloat(value);
		}

		return defaultValue;
	}

	public static String removeEnclosingQuotes(final String string) {
		return string.replaceAll("^\"|\"$", "");
	}

	public static <T> T[] toArray(final Collection<T> collection, final Class<T> arrayType) {
		T[] ret = null;

		ret = collection.toArray((T[]) Array.newInstance(arrayType, 0));

		return ret;
	}
}
