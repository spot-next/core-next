package at.spot.jfly.event;

import at.spot.jfly.Component;

public abstract class AbstractEvent {
	private final Component source;

	public AbstractEvent(final Component source) {
		this.source = source;
	}

	public Component getSource() {
		return this.source;
	}
}
