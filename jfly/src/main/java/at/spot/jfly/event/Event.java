package at.spot.jfly.event;

import java.util.Map;

import at.spot.jfly.ui.base.Component;

public class Event {
	private final JsEvent eventType;
	private final Component source;
	private final Map<String, Object> payload;

	public Event(final JsEvent eventType, final Component source, final Map<String, Object> payload) {
		this.source = source;
		this.eventType = eventType;
		this.payload = payload;
	}

	public Component getSource() {
		return this.source;
	}

	public JsEvent getEventType() {
		return eventType;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}
}
