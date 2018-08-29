package io.spotnext.core.infrastructure.service.impl;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.AccessControlService;
import io.spotnext.core.types.Item;

/**
 * This is a dummy implementation of the {@link io.spotnext.core.infrastructure.service.AccessControlService} that just
 * returns always true.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DummyAccessControlService implements AccessControlService {

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean accessAllowed(final Class<T> type) {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean accessAllowed(final Class<T> type, final String property) {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean accessAllowed(final T type) {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends Item> boolean accessAllowed(final T type, final String property) {
		return true;
	}

}
