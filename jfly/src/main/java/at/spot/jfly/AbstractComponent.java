package at.spot.jfly;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import at.spot.jfly.event.AbstractEvent;
import at.spot.jfly.event.EventHandler;
import at.spot.jfly.event.JsEvents;
import at.spot.jfly.event.OnClickEvent;
import j2html.tags.ContainerTag;

public abstract class AbstractComponent implements Component {

	protected String tagName;
	protected String uuid;

	private boolean visible = true;

	private final Set<String> styleClasses = new HashSet<>();

	/*
	 * Event handlers
	 */
	protected Map<Class<? extends AbstractEvent>, EventHandler<? extends AbstractEvent>> eventHandlers = new HashMap<>();
	protected Set<JsEvents> registeredEvents = new HashSet<>();

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

	public Set<JsEvents> registeredEvents() {
		return this.registeredEvents;
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
			controller().invoke(this, "show");
		} else {
			controller().invoke(this, "hide");
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
	public <C extends AbstractComponent> C onClick(final EventHandler<OnClickEvent> handler) {
		if (handler != null) {
			registeredEvents.add(JsEvents.click);
			this.eventHandlers.put(OnClickEvent.class, handler);
		} else {
			registeredEvents.remove(JsEvents.click);
			this.eventHandlers.remove(OnClickEvent.class);
		}

		return (C) this;
	}

	@Override
	public <E extends AbstractEvent> void handleEvent(final E event) {
		final EventHandler<E> handler = (EventHandler<E>) eventHandlers.get(event.getClass());

		handler.handle(event);
	}
}
