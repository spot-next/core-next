package io.spotnext.core.infrastructure.support;

import spark.Request;

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
