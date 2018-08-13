package io.spotnext.core.infrastructure.service;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Provides infrastructure for publishing events.
 */
public interface EventService {
	/**
	 * Synchronously notify all <strong>matching</strong> listeners registered
	 * with this application of an application event. Events may be framework
	 * events (such as RequestHandledEvent) or application-specific events.
	 * 
	 * @param event
	 *            the event to publish
	 * @see org.springframework.web.context.support.RequestHandledEvent
	 */
	<E extends ApplicationEvent> void publishEvent(final E event);

	/**
	 * Multicast the given application event to appropriate listeners.
	 * 
	 * @param event
	 *            the event to multicast
	 */
	<E extends ApplicationEvent> void multicastEvent(E event);

	/**
	 * Registers an event listener. Useful in spring child contexts as those
	 * listeners are not being registered automatically.
	 * 
	 * @param listener
	 *            the listener to register
	 */
	void registerListener(ApplicationListener<?> listener);
}
