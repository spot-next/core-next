package io.spotnext.core.management.service;

/**
 * <p>ManagableService interface.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
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
	 * If this returns true, the {@link io.spotnext.core.management.service.ManagableService#init()} method should
	 * also start the service.
	 *
	 * @return a boolean.
	 */
	boolean isAutoStart();

	/**
	 * Stop the service. No data may be lost. The service must also be startable
	 * again.
	 */
	void stop();
}
