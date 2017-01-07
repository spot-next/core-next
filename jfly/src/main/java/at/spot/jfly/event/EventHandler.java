package at.spot.jfly.event;

public interface EventHandler<E extends AbstractEvent> {
	void handle(E event);
}
