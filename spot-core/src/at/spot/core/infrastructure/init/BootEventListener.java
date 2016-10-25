package at.spot.core.infrastructure.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import at.spot.core.infrastructure.event.SystemBootCompleteEvent;
import at.spot.core.infrastructure.service.LoggingService;

@Component
class BootEventListener implements ApplicationListener<ContextStartedEvent> {

	@Autowired
	protected LoggingService loggingService;

	public BootEventListener() {
		System.out.println("");
	}

	@EventListener
	protected void onContextRefreshedEvent(ContextRefreshedEvent event) {
		loggingService.info("Server start finished.");
	}

	@EventListener
	protected void onBootComplete(SystemBootCompleteEvent event) {
		loggingService.info("Server start finished.");
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent paramE) {
		loggingService.info("Server start finished.");
	}
}
