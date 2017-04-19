package at.spot.core.management.service;

public interface ManagableService {

	/**
	 * Setup all that's necessary to start the service.
	 */
	void init();

	/**
	 * Start the service. It must also be stoppable again
	 */
	void start();

	/**
	 * If this returns true, the {@link ManagableService#init()} method should
	 * also start the service.
	 */
	boolean isAutoStart();

	/**
	 * Stop the service. No data may be lost. The service must also be startable
	 * again.
	 */
	void stop();
}
