package io.spotnext.core.infrastructure.service;

import java.util.concurrent.Callable;

import io.spotnext.core.infrastructure.http.Session;

/**
 * This service provides basic backend sessions handling. It does not integrate
 * into spring or any other web framework.
 */
public interface SessionService {

	/**
	 * Creates a new {@link Session} object and sets it for the current thread.
	 */
	Session createSession(boolean registerAsCurrentSession);

	/**
	 * Returns the {@link Session} associates with the current thread. If there is
	 * no session registered yet, a new one is created and automatically registered.
	 */
	Session getCurrentSession();

	/**
	 * Sets the given session for the current thread.
	 */
	void setCurrentSession(Session session);

	/**
	 * Returns the {@link Session} with the given id.
	 */
	Session getSession(String sessionId);

	/**
	 * Closes/invalidate the {@link Session} with the given id.
	 */
	void closeSession(String sessionID);

	/**
	 * Executes the given {@link Callable} in another thread setting the using the
	 * session of the given id. This is useful to gain different privileges.
	 */
	<T> T executeInSessionContext(String sessionId, Callable<T> callable);

	/**
	 * Executes the given {@link Callable} in another thread using the system (=
	 * root) session context.
	 */
	<T> T executeInSystemSessionContext(Callable<T> callable);

}
