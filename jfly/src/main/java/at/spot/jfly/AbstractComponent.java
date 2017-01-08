package at.spot.jfly;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import at.spot.jfly.event.Event;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.JsEvent;
import j2html.tags.ContainerTag;

public abstract class AbstractComponent implements Component, Comparable<AbstractComponent> {

	protected String tagName;
	protected String uuid;

	private boolean visible = true;

	private final Set<String> styleClasses = new HashSet<>();

	/*
	 * Event handlers
	 */
	protected Map<JsEvent, EventHandler> eventHandlers = new HashMap<>();

	/*
	 * Initialization
	 */

	protected AbstractComponent() {
		uuid = UUID.randomUUID().toString();
		ComponentController.instance().registerComponent(this);
	}

	protected AbstractComponent(final String tagName) {
		this();
		this.tagName = tagName;
	}

	/*
	 * Properties
	 */

	public Set<JsEvent> registeredEvents() {
		return this.eventHandlers.keySet();
	}

	@Override
	public String uuid() {
		return uuid;
	}

	public boolean visibility() {
		return visible;
	}

	public <C extends AbstractComponent> C visibility(final boolean visible) {
		this.visible = visible;

		if (visible) {
			controller().invokeFunctionCall("jfly", "showComponent", this.uuid());
		} else {
			controller().invokeFunctionCall("jfly", "hideComponent", this.uuid());
		}

		return (C) this;
	}

	protected ComponentController controller() {
		return ComponentController.instance();
	}

	protected String getStyleClasses() {
		String classes = StringUtils.join(styleClasses, " ");

		if (!visibility()) {
			classes += " hidden";
		}

		return classes;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = new ContainerTag(tagName);

		raw.attr("uuid", uuid());
		raw.attr("registeredEvents", StringUtils.join(registeredEvents(), " "));

		return raw;
	}

	/*
	 * Events
	 */

	@Override
	public <C extends AbstractComponent> C onEvent(final JsEvent eventType, final EventHandler handler) {
		if (handler != null) {
			this.eventHandlers.put(eventType, handler);
			controller().invokeFunctionCall("jfly", "registerEvent", this.uuid(), eventType);
		} else {
			this.eventHandlers.remove(eventType);
			controller().invokeFunctionCall("jfly", "unregisterEvent", this.uuid(), eventType);
		}

		return (C) this;
	}

	@Override
	public void handleEvent(final Event event) {
		final EventHandler handler = eventHandlers.get(event.getEventType());

		handler.handle(event);
	}

	@Override
	public int compareTo(final AbstractComponent o) {
		if (o == null) {
			return 1;
		}

		return uuid().compareTo(o.uuid());
	}
}
