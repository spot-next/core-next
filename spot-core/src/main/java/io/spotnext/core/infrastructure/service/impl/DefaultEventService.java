package io.spotnext.core.infrastructure.service.impl;

import javax.annotation.Resource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.EventService;

/**
 * <p>DefaultEventService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultEventService extends AbstractService implements EventService {

	private boolean isReady = false;

	// asynchronous events
	@Resource
	protected ApplicationEventMulticaster applicationEventMulticaster;

	// synchronous events
	@Resource
	protected ApplicationEventPublisher applicationEventPublisher;

	@EventListener
	protected void onApplicationReady(final ApplicationReadyEvent event) {
		isReady = true;
	}

	/** {@inheritDoc} */
	@Override
	public <E extends ApplicationEvent> void publishEvent(final E event) {
		if (isReady) {
			applicationEventPublisher.publishEvent(event);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <E extends ApplicationEvent> void multicastEvent(final E event) {
		if (isReady) {
			applicationEventMulticaster.multicastEvent(event);
		}
	}

}
