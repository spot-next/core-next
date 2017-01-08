package at.spot.jfly;

import at.spot.jfly.event.Event;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.JsEvent;
import j2html.tags.ContainerTag;

public interface Component {

	String uuid();

	/**
	 * Renders the raw j2html item - accessible with {@link #raw()}.
	 */
	ContainerTag build();

	// <C extends AbstractComponent> C onClick(final EventHandler<OnClickEvent>
	// handler);

	// <C extends AbstractComponent> C onMouseOver(final
	// EventHandler<OnMouseOverEvent> handler);

	<C extends AbstractComponent> C onEvent(JsEvent eventType, EventHandler handler);

	<E extends Event> void handleEvent(final E event);
}
