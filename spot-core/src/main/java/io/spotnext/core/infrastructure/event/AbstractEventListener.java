package io.spotnext.core.infrastructure.event;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import io.spotnext.core.infrastructure.service.EventService;

public abstract class AbstractEventListener<E extends ApplicationEvent> implements ApplicationListener<E> {

	@Resource
	private EventService eventService;

	@PostConstruct
	public void init() {
		eventService.registerListener(this);
	}
}
