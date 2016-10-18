package at.spot.core.infrastructure.service;

import java.net.SocketException;

import at.spot.core.management.exception.RemoteServiceInitException;

/**
 * This is the basis for any remote interface service, such as HTTP or SSH
 * service.
 *
 */
public interface RemoteInterfaceService {

	/**
	 * Initializes the remote service.
	 * 
	 * @throws RemoteServiceInitException
	 * @throws SocketException
	 */
	void init() throws RemoteServiceInitException, SocketException;

	/**
	 * Gets the port that is being used for this service.
	 * 
	 * @param port
	 */
	int getPort();
}
