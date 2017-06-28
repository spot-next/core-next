package at.spot.core.infrastructure.support.init;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import at.spot.core.infrastructure.exception.ModuleInitializationException;

public abstract class ModuleInit {

	/**
	 * This is a hook to customize the initialization process. It is called after
	 * {@link Bootstrap} has finished doing the basic initialization (load config
	 * properties and spring configuration).
	 */
	protected abstract void initialize() throws ModuleInitializationException;

	/**
	 * Called when the spring application context has been initialized.
	 * 
	 * @param event
	 * @throws ModuleInitializationException
	 */
	@EventListener
	protected void onApplicationEvent(final ApplicationReadyEvent event) throws ModuleInitializationException {
		initialize();
	}
}
