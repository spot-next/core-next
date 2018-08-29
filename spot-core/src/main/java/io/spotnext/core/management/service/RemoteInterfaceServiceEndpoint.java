package io.spotnext.core.management.service;

import java.net.InetAddress;
import java.net.SocketException;

import io.spotnext.core.management.exception.RemoteServiceInitException;

/**
 * This is the basis for any remote interface service, such as HTTP or SSH
 * service.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface RemoteInterfaceServiceEndpoint {

	/**
	 * Initializes the remote service.
	 *
	 * @throws io.spotnext.core.management.exception.RemoteServiceInitException
	 * @throws SocketException if any.
	 */
	void init() throws RemoteServiceInitException;

	/**
	 * Is called when a shutdown signal is received (eg. from spring).
	 *
	 * @throws io.spotnext.core.management.exception.RemoteServiceInitException
	 */
	void shutdown() throws RemoteServiceInitException;

	/**
	 * Gets the port that is being used for this service.
	 *
	 * @return a int.
	 */
	int getPort();

	/**
	 * Gets the address on which the service is listening.
	 *
	 * @return a {@link java.net.InetAddress} object.
	 */
	InetAddress getBindAddress();
}
