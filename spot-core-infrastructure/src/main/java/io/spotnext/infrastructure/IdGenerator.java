package io.spotnext.infrastructure;

import java.util.UUID;

public class IdGenerator {
	public static String createStringIdFromRandomUUID() {
		final UUID uuid = java.util.UUID.randomUUID();
		return uuid.toString();
	}

	public static Long createLongIdFromRandomUUID() {
		return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
	}

	public static int createSerialIntId() {
		return 0;
	}
}