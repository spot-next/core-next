package at.spot.core.infrastructure.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.event.SystemBootCompleteEvent;
import at.spot.core.infrastructure.service.EventService;
import at.spot.core.infrastructure.service.LoggingService;

@Component
class BootEventListener {

	@Autowired
	protected EventService eventService;

	@Autowired
	protected LoggingService loggingService;

	public BootEventListener() {
		System.out.println("");
	}

	@EventListener
	public void onContextStarted(final ContextStartedEvent event) {
		eventService.publishEvent(new SystemBootCompleteEvent(this));
	}

	@EventListener
	protected void onContextRefreshedEvent(final ContextRefreshedEvent event) {
		//
	}

	@EventListener
	public void onContextStoppedEvent(final ContextStoppedEvent event) {
		loggingService.info("Server stopped.");
	}

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
