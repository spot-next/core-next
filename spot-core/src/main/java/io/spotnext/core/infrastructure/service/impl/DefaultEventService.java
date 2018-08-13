package io.spotnext.core.infrastructure.service.impl;

import javax.annotation.Resource;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.EventService;
import io.spotnext.core.infrastructure.support.spring.Registry;

@Service
public class DefaultEventService extends AbstractService implements EventService {

	// asynchronous events
	@Resource
	protected ApplicationEventMulticaster applicationEventMulticaster;

	// synchronous events
	@Resource
	protected ApplicationEventPublisher applicationEventPublisher;

	@Override
	public <E extends ApplicationEvent> void publishEvent(final E event) {
		applicationEventPublisher.publishEvent(event);
	}

	@Override
	public <E extends ApplicationEvent> void multicastEvent(final E event) {
		applicationEventMulticaster.multicastEvent(event);
	}

	@Override
	public void registerListener(final ApplicationListener<?> listener) {
		applicationEventMulticaster.addApplicationListener(listener);
		((AbstractApplicationContext) Registry.getApplicationContext()).addApplicationListener(listener);
	}

}
