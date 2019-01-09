package io.spotnext.core.persistence.generator;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.spotnext.infrastructure.IdGenerator;
import io.spotnext.infrastructure.type.Item;

@Service
public class RandomUUIDGenerator implements IdGenerator {
	@Override
	public String createStringId(Class<? extends Item> itemType) {
		final UUID uuid = java.util.UUID.randomUUID();
		return uuid.toString();
	}

	@Override
	public Long createLongId(Class<? extends Item> itemType) {
		return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
	}

}