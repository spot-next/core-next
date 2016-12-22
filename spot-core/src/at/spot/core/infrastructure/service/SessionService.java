package at.spot.core.infrastructure.service;

import java.util.concurrent.Callable;

import at.spot.core.infrastructure.type.Session;

public interface SessionService {

	/**
	 * Creates a new {@link Session} object.
	 * 
	 * @return
	 */
	Session createSession();

	/**
	 * Returns the {@link Session} associates with the current thread. If there
	 * is non registered, {@link #createSession()} is called and given session
	 * object is returned.
	 * 
	 * @return
	 */
	Session getCurrentSession();

	/**
	 * Returns the {@link Session} with the given id.
	 * 
	 * @param sessionId
	 * @return
	 */
	Session getSession(String sessionId);

	/**
	 * Closes/invalidate the {@link Session} with the given id.
	 * 
	 * @param sessionID
	 */
	void closeSession(String sessionID);

	/**
	 * Executes the given {@link Callable} in another session context. This is
	 * useful to gain different privileges.
	 * 
	 * @param sessionId
	 * @param callable
	 * @return
	 */
	<T> T executeInSessionContext(String sessionId, Callable<T> callable);

	/**
	 * Executes the given {@link Callable} in the system (=root) session
	 * context.
	 * 
	 * @param callable
	 * @return
	 */
	<T> T executeInSystemSessionContext(Callable<T> callable);

}
