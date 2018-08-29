package io.spotnext.core.infrastructure.support.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.spotnext.core.infrastructure.event.SystemBootCompleteEvent;
import io.spotnext.core.infrastructure.service.EventService;
import io.spotnext.core.infrastructure.service.LoggingService;

@Component
class BootEventListener {

	@Autowired
	protected EventService eventService;

	@Autowired
	protected LoggingService loggingService;

	/**
	 * <p>Constructor for BootEventListener.</p>
	 */
	public BootEventListener() {
		System.out.println("");
	}

	/**
	 * <p>onContextStarted.</p>
	 *
	 * @param event a {@link org.springframework.context.event.ContextStartedEvent} object.
	 */
	@EventListener
	public void onContextStarted(final ContextStartedEvent event) {
		eventService.publishEvent(new SystemBootCompleteEvent(this));
	}

	@EventListener
	protected void onContextRefreshedEvent(final ContextRefreshedEvent event) {
		//
	}

	/**
	 * <p>onContextStoppedEvent.</p>
	 *
	 * @param event a {@link org.springframework.context.event.ContextStoppedEvent} object.
	 */
	@EventListener
	public void onContextStoppedEvent(final ContextStoppedEvent event) {
		loggingService.info("Server stopped.");
	}

	/**
	 * <p>onContextClosedEvent.</p>
	 *
	 * @param event a {@link org.springframework.context.event.ContextClosedEvent} object.
	 */
	@EventListener
	public void onContextClosedEvent(final ContextClosedEvent event) {
		loggingService.info("Spring context closed.");
	}

	/*
	 * Custom events
	 */

	@EventListener
	protected void onBootComplete(final SystemBootCompleteEvent event) {
		loggingService.info("Server start finished.");
	}
}
