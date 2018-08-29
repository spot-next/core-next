package io.spotnext.core.infrastructure.event;

import org.springframework.context.ApplicationEvent;

/**
 * <p>SystemBootCompleteEvent class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class SystemBootCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for SystemBootCompleteEvent.</p>
	 *
	 * @param source a {@link java.lang.Object} object.
	 */
	public SystemBootCompleteEvent(Object source) {
		super(source);
	}
}
