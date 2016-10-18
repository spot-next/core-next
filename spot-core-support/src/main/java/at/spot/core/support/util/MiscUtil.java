package at.spot.core.support.util;

import java.io.Closeable;

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
}
