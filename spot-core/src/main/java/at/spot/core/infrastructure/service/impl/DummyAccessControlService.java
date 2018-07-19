package at.spot.core.infrastructure.service.impl;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.AccessControlService;
import at.spot.core.types.Item;

/**
 * This is a dummy implementation of the {@link AccessControlService} that just
 * returns always true.
 *
 */
@Service
public class DummyAccessControlService implements AccessControlService {

	@Override
	public <T extends Item> boolean accessAllowed(final Class<T> type) {
		return true;
	}

	@Override
	public <T extends Item> boolean accessAllowed(final Class<T> type, final String property) {
		return true;
	}

	@Override
	public <T extends Item> boolean accessAllowed(final T type) {
		return true;
	}

	@Override
	public <T extends Item> boolean accessAllowed(final T type, final String property) {
		return true;
	}

}
