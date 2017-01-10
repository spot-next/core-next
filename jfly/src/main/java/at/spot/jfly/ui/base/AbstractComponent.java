package at.spot.jfly.ui.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import at.spot.jfly.ComponentController;
import at.spot.jfly.event.Event;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.JsEvent;
import at.spot.jfly.style.ComponentType;
import j2html.tags.ContainerTag;

public abstract class AbstractComponent implements Component, Comparable<AbstractComponent> {

	private final String uuid;

	private transient String tagName;
	private transient ComponentType componentType;
	private boolean visible = true;

	private final Set<String> styleClasses = new HashSet<>();

	/*
	 * Event handlers
	 */
	protected transient Map<JsEvent, EventHandler> eventHandlers = new HashMap<>();

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

	protected String tagName() {
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

	public <C extends AbstractComponent> C componentType(final ComponentType componentType) {
		this.componentType = componentType;
		return (C) this;
	}

	public ComponentType componentType() {
		return this.componentType;
	}

	public <C extends AbstractComponent> C visibility(final boolean visible) {
		this.visible = visible;

		// if (visible) {
		// updateClient("jfly", "showComponent", this.uuid());
		// } else {
		// updateClient("jfly", "hideComponent", this.uuid());
		// }

		return (C) this;
	}

	public <C extends AbstractComponent> C addStyleClasses(final String... styleClasses) {
		final List<String> styles = Arrays.stream(styleClasses).filter(s -> StringUtils.isNotBlank(s))
				.collect(Collectors.toList());

		this.styleClasses.addAll(styles);

		return (C) this;
	}

	public <C extends AbstractComponent> C removeStyleClasses(final String... styleClasses) {
		final List<String> styles = Arrays.stream(styleClasses).filter(s -> StringUtils.isNotBlank(s))
				.collect(Collectors.toList());

		this.styleClasses.removeAll(styles);
		// updateClientComponent("removeClass", styleClass);
		return (C) this;

	}

	/**
	 * Returns an immutable set of the style classes.
	 */
	public Set<String> styleClasses() {
		return Collections.unmodifiableSet(styleClasses);
	}

	/*
	 * Events
	 */

	@Override
	public <C extends AbstractComponent> C onEvent(final JsEvent eventType, final EventHandler handler) {
		if (handler != null) {
			this.eventHandlers.put(eventType, handler);
			// updateClient("jfly", "registerEvent", this.uuid(), eventType);
		} else {
			this.eventHandlers.remove(eventType);
			// updateClient("jfly", "unregisterEvent", this.uuid(), eventType);
		}

		return (C) this;
	}

	@Override
	public void handleEvent(final Event event) {
		final EventHandler handler = eventHandlers.get(event.getEventType());

		handler.handle(event);
	}

	/*
	 * INTERNAL
	 */

	protected ComponentController controller() {
		return ComponentController.instance();
	}

	protected String getCssStyleString() {
		String classes = StringUtils.join(styleClasses, " ");

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

		for (JsEvent event : registeredEvents()) {
			raw.attr("v-on:" + event.toString(), "handleEvent");
		}

		if (componentType != null) {
			raw.attr("type", componentType.toString());
		}

		return raw;
	}

	@Override
	public <C extends AbstractComponent> C redraw() {
		updateClientComponent("replace", build().render());
		return (C) this;
	}

	protected void updateClientComponent() {
		if (controller().isCalledInRequest()) {
			controller().updateComponentData(this);
		}
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

	@Override
	public int compareTo(final AbstractComponent o) {
		if (o == null) {
			return 1;
		}

		return uuid().compareTo(o.uuid());
	}
}
