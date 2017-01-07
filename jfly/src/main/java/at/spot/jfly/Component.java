package at.spot.jfly;

import at.spot.jfly.event.AbstractEvent;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.OnClickEvent;
import j2html.tags.ContainerTag;

public interface Component {

	String uuid();

	/**
	 * Renders the raw j2html item - accessible with {@link #raw()}.
	 */
	ContainerTag build();

	<C extends AbstractComponent> C onClick(final EventHandler<OnClickEvent> handler);

	<E extends AbstractEvent> void handleEvent(final E event);
}
