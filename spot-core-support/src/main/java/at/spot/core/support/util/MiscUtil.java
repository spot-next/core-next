package at.spot.core.support.util;

import java.io.Closeable;

import org.apache.commons.lang3.StringUtils;

public class MiscUtil {

	/**
	 * Calls close() on all given objects that implement the {@link Closeable}
	 * interface. Doesn't throw any exceptions at all.
	 * 
	 * @param closableObject
	 */
	public static void closeQuietly(Closeable... closableObjects) {
		try {
			for (Closeable c : closableObjects) {
				if (c != null) {
					c.close();
				}
			}
		} catch (Exception e) {
			// ignore exceptions
		}
	}
	
	public static int intOrDefault(String value, int defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Integer.parseInt(value);
		}
		
		return defaultValue;
	}
	
	public static double longOrDefault(String value, long defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Long.parseLong(value);
		}
		
		return defaultValue;
	}
	
	public static double doubleOrDefault(String value, double defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Double.parseDouble(value);
		}
		
		return defaultValue;
	}
	
	public static double floatOrDefault(String value, float defaultValue) {
		if (StringUtils.isNotBlank(value)) {
			return Float.parseFloat(value);
		}
		
		return defaultValue;
	}
}
