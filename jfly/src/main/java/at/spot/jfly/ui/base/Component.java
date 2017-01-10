package at.spot.jfly.ui.base;

import com.google.gson.Gson;

import at.spot.jfly.event.Event;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.JsEvent;
import j2html.tags.ContainerTag;

public interface Component {

	/**
	 * This unique identifier is used to identify the component on the client
	 * and bind it to the server-side component instance.
	 */
	String uuid();

	/**
	 * Renders the raw j2html item - accessible with {@link #raw()}.
	 */
	ContainerTag build();

	/**
	 * Registers an event handler for the given javascript event. The event can
	 * be unregistered by passing null as handler.
	 * 
	 * @param eventType
	 * @param handler
	 * @return
	 */
	<C extends AbstractComponent> C onEvent(JsEvent eventType, EventHandler handler);

	/**
	 * Pass an event for the component to process.
	 * 
	 * @param event
	 */
	<E extends Event> void handleEvent(final E event);

	/**
	 * Redraws the component on the client side.
	 * 
	 * @return
	 */
	<C extends AbstractComponent> C redraw();

	/**
	 * Serializes the component's state into a json string.
	 */
	default String toJson() {
		return new Gson().toJson(this);
	}
}
