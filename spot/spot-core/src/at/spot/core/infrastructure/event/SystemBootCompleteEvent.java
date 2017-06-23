package at.spot.core.infrastructure.event;

import org.springframework.context.ApplicationEvent;

public class SystemBootCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public SystemBootCompleteEvent(Object source) {
		super(source);
	}
}
