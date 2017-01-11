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
import at.spot.jfly.style.Style;
import io.gsonfire.annotations.ExposeMethodResult;
import j2html.tags.ContainerTag;

public abstract class AbstractComponent implements Component, Comparable<AbstractComponent> {

	private final String uuid;

	private transient String tagName;
	private transient ComponentType componentType;
	private final transient Set<String> styleClasses = new HashSet<>();
	private boolean visible = true;

	/*
	 * Event handlers
	 */
	protected transient Map<JsEvent, EventHandler> eventHandlers = new HashMap<>();

	/*
	 * Initialization
	 */

	protected AbstractComponent() {
		// this strange uid is necessary, as we are also using this for vue
		// property binding
		// there are no dashes allowed, and each uuid must start with a letter.
		uuid = "comp" + Math.abs(UUID.randomUUID().toString().hashCode());
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
		updateClientComponent();
		return (C) this;
	}

	public ComponentType componentType() {
		return this.componentType;
	}

	public <C extends AbstractComponent> C visibe(final boolean visible) {
		this.visible = visible;
		redrawClientComponent();
		return (C) this;
	}

	public <C extends AbstractComponent> C addStyleClasses(final String... styleClasses) {
		final List<String> styles = Arrays.stream(styleClasses).filter(s -> StringUtils.isNotBlank(s))
				.collect(Collectors.toList());

		this.styleClasses.addAll(styles);
		redrawClientComponent();
		return (C) this;
	}

	public <C extends AbstractComponent> C removeStyleClasses(final String... styleClasses) {
		final List<String> styles = Arrays.stream(styleClasses).filter(s -> StringUtils.isNotBlank(s))
				.collect(Collectors.toList());

		this.styleClasses.removeAll(styles);
		redrawClientComponent();
		return (C) this;
	}

	public <C extends AbstractComponent> C addStyleClasses(final Style... styles) {
		final List<String> stylesClasses = Arrays.stream(styles).filter((s) -> s != null).map(s -> s.internalName())
				.collect(Collectors.toList());
		this.styleClasses.addAll(stylesClasses);
		redrawClientComponent();
		return (C) this;
	}

	public <C extends AbstractComponent> C removeStyleClasses(final Style... styles) {
		final List<String> stylesClasses = Arrays.stream(styles).filter((s) -> s != null).map(s -> s.internalName())
				.collect(Collectors.toList());

		this.styleClasses.removeAll(stylesClasses);
		redrawClientComponent();
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
		} else {
			this.eventHandlers.remove(eventType);
		}

		updateClientComponent();

		return (C) this;
	}

	@Override
	public void handleEvent(final Event event) {
		final EventHandler handler = eventHandlers.get(event.getEventType());

		handler.handle(event);
	}

	@Override
	public <C extends AbstractComponent> C redraw() {
		redrawClientComponent();
		return (C) this;
	}

	/*
	 * INTERNAL
	 */

	protected ComponentController controller() {
		return ComponentController.instance();
	}

	@ExposeMethodResult("styleClasses")
	protected String getCssStyleString() {
		String classes = StringUtils.join(styleClasses, " ");

		if (!visibility()) {
			classes += " hidden";
		}

		return classes;
	}

	@ExposeMethodResult("componentType")
	protected String getComponentType() {
		return componentType != null ? componentType.internalName() : null;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = new ContainerTag(tagName);

		raw.withClass(getCssStyleString());
		raw.attr("uuid", uuid());

		raw.attr("registeredEvents", StringUtils.join(registeredEvents(), " "));

		if (componentType != null) {
			raw.attr("type", componentType.toString());
		}

		return raw;
	}

	protected void redrawClientComponent() {
		if (controller().isCalledInRequest()) {
			controller().redrawComponentData(this);
		}
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
