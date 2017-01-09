package at.spot.jfly;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import at.spot.jfly.event.Event;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.JsEvent;
import at.spot.jfly.style.Style;
import j2html.tags.ContainerTag;

public abstract class AbstractComponent implements Component, Comparable<AbstractComponent> {

	protected String tagName;
	protected String uuid;

	private boolean visible = true;

	private final Set<Style> styles = new HashSet<>();
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

	public String tagName() {
		return this.tagName;
	}

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
			updateClient("jfly", "showComponent", this.uuid());
		} else {
			updateClient("jfly", "hideComponent", this.uuid());
		}

		return (C) this;
	}

	protected ComponentController controller() {
		return ComponentController.instance();
	}

	public <C extends AbstractComponent> C addStyle(final Style style) {
		if (style != null) {
			this.styles.add(style);
			updateClientComponent("addClass", style);
		}

		return (C) this;
	}

	public <C extends AbstractComponent> C addStyleClass(final String styleClass) {
		if (StringUtils.isNotBlank(styleClass)) {
			this.styleClasses.add(styleClass);
			updateClientComponent("addClass", styleClass);
		}

		return (C) this;
	}

	public <C extends AbstractComponent> C removeStyle(final Style style) {
		if (style != null) {
			this.styles.remove(style);
			updateClientComponent("removeClass", style);
		}

		return (C) this;
	}

	public <C extends AbstractComponent> C removeStyleClass(final String styleClass) {
		if (StringUtils.isNotBlank(styleClass)) {
			this.styleClasses.remove(styleClass);
			updateClientComponent("removeClass", styleClass);
		}
		return (C) this;
	}

	/**
	 * Returns an immutable set of the styles.
	 */
	public Set<Style> styles() {
		return Collections.unmodifiableSet(styles);
	}

	/**
	 * Returns an immutable set of the style classes.
	 */
	public Set<String> styleClasses() {
		return Collections.unmodifiableSet(styleClasses);
	}

	protected String getCssStyleString() {
		String classes = StringUtils.join(styles, " ") + " " + StringUtils.join(styleClasses, " ");

		if (!visibility()) {
			classes += " hidden";
		}

		return classes;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = new ContainerTag(tagName);

		raw.withClass(getCssStyleString());
		raw.attr("uuid", uuid());
		raw.attr("registeredEvents", StringUtils.join(registeredEvents(), " "));

		return raw;
	}

	@Override
	public <C extends AbstractComponent> C redraw() {
		updateClientComponent("replace", build().render());
		return (C) this;
	}

	protected void updateClientComponent(final String method, final Object... params) {
		if (controller().isCalledInRequest()) {
			controller().invokeComponentManipulation(this, method, params);
		}
	}

	protected void updateClient(final String object, final String function, final Object... params) {
		if (controller().isCalledInRequest()) {
			controller().invokeFunctionCall(object, function, params);
		}
	}

	/*
	 * Events
	 */

	@Override
	public <C extends AbstractComponent> C onEvent(final JsEvent eventType, final EventHandler handler) {
		if (handler != null) {
			this.eventHandlers.put(eventType, handler);
			updateClient("jfly", "registerEvent", this.uuid(), eventType);
		} else {
			this.eventHandlers.remove(eventType);
			updateClient("jfly", "unregisterEvent", this.uuid(), eventType);
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
