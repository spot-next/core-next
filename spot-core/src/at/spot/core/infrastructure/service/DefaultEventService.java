package at.spot.core.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.service.impl.AbstractService;

@Component
public class DefaultEventService extends AbstractService implements EventService {

	@Autowired
	protected ApplicationEventPublisher publisher;

	@Override
	public void publishEvent(ApplicationEvent applicationEvent) {
		publisher.publishEvent(applicationEvent);
	}

	@Override
	public void publishEvent(Object eventObject) {
		publisher.publishEvent(eventObject);
	}
}
