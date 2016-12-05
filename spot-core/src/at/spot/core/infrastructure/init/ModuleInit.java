package at.spot.core.infrastructure.init;

import javax.annotation.PostConstruct;

public abstract class ModuleInit {

	/**
	 * Initializes a module with the given parent application context (from the
	 * {@link Bootstrap}).
	 * 
	 * @param parentContext
	 */
	@PostConstruct
	public void init() {
		initialize();
	}

	/**
	 * This is a hook to customize the initialization process. It is called
	 * after {@link Bootstrap} has finished doing the basic initialization (load
	 * config properties and spring configuration).
	 */
	public abstract void initialize();
}
