package io.spotnext.core.infrastructure.support;

import spark.Request;

/**
 * <p>HttpRequestHolder class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class HttpRequestHolder {
	private static ThreadLocal<Request> currentRequest = new ThreadLocal<>();

	/**
	 * Sets the given request object as the current request of this thread.
	 *
	 * @param request the request to store for this thread.
	 */
	public static void setRequest(Request request) {
		currentRequest.set(request);
	}

	/**
	 * <p>getRequest.</p>
	 *
	 * @return the current request object for this thread, or null.
	 */
	public static Request getRequest() {
		return currentRequest.get();
	}

	/**
	 * Clears the current request for this thread.
	 */
	public static void clear() {
		setRequest(null);
	}
}
