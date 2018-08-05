package io.spotnext.core.infrastructure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.EventService;

@Service
public class DefaultEventService extends AbstractService implements EventService {

	@Autowired
	protected ApplicationEventPublisher publisher;

	@Override
	public void publishEvent(final ApplicationEvent applicationEvent) {
		publisher.publishEvent(applicationEvent);
	}

	@Override
	public void publishEvent(final Object eventObject) {
		publisher.publishEvent(eventObject);
	}
}
