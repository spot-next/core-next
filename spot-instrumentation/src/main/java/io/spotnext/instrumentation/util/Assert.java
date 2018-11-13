package io.spotnext.instrumentation.util;

public class Assert {
	public static void assertTrue(boolean value, String message) {
		if (!value) {
			throw new IllegalStateException(message);
		}
	}
}
