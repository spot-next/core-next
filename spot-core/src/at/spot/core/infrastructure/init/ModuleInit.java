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
	 * Implements the module's initialization process.
	 */
	public abstract void initialize();
}
