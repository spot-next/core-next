package io.spotnext.core.infrastructure.service;

import java.util.Map;
import java.util.concurrent.Callable;

import io.spotnext.core.infrastructure.http.Session;

/**
 * This service provides basic backend sessions handling. It does not integrate
 * into spring or any other web framework.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SessionService {

	/**
	 * Creates a new {@link io.spotnext.infrastructure.http.Session} object and sets it for the current thread.
	 *
	 * @param registerAsCurrentSession a boolean.
	 * @return a {@link io.spotnext.infrastructure.http.Session} object.
	 */
	Session createSession(boolean registerAsCurrentSession);

	/**
	 * Returns the {@link io.spotnext.infrastructure.http.Session} associates with the current thread. If there is
	 * no session registered yet, a new one is created and automatically registered.
	 *
	 * @return a {@link io.spotnext.infrastructure.http.Session} object.
	 */
	Session getCurrentSession();

	/**
	 * Sets the given session for the current thread.
	 *
	 * @param session a {@link io.spotnext.infrastructure.http.Session} object.
	 */
	void setCurrentSession(Session session);

	/**
	 * Returns the {@link io.spotnext.infrastructure.http.Session} with the given id.
	 *
	 * @param sessionId a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.http.Session} object.
	 */
	Session getSession(String sessionId);

	/**
	 * Closes/invalidate the {@link io.spotnext.infrastructure.http.Session} with the given id.
	 *
	 * @param sessionID a {@link java.lang.String} object.
	 */
	void closeSession(String sessionID);

	/**
	 * Executes the given {@link java.util.concurrent.Callable} in another thread setting the using the
	 * session of the given id. This is useful to gain different privileges.
	 *
	 * @param sessionId a {@link java.lang.String} object.
	 * @param callable  a {@link java.util.concurrent.Callable} object.
	 * @return a T object.
	 */
	<T> T executeInSessionContext(String sessionId, Callable<T> callable);

	/**
	 * Executes the given {@link java.util.concurrent.Callable} in another thread using the system (=
	 * root) session context.
	 *
	 * @param callable a {@link java.util.concurrent.Callable} object.
	 * @return a T object.
	 */
	<T> T executeInSystemSessionContext(Callable<T> callable);

	/**
	 * @return Returns all currently registered sessions, grouped by the session id.
	 */
	Map<String, Session> getAllSessions();
}
