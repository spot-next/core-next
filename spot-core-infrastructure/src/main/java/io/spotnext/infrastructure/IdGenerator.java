package io.spotnext.infrastructure;

import java.util.UUID;

public class IdGenerator {
	public static String createStringId() {
		final UUID uuid = java.util.UUID.randomUUID();
		return uuid.toString();
	}

	public static Long createLongId() {
		return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
	}

}