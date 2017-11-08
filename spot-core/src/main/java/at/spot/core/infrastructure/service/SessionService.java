package at.spot.core.infrastructure.service;

import java.util.concurrent.Callable;

import org.springframework.lang.NonNull;

import at.spot.core.infrastructure.http.Session;

/**
 * This service provides basic backend sessions handling. It does not integrate
 * into spring or any other web framework.
 */
public interface SessionService {

	/**
	 * Creates a new {@link Session} object and sets it for the current thread.
	 * 
	 * @return
	 */
	Session createSession(boolean registerAsCurrentSession);

	/**
	 * Returns the {@link Session} associates with the current thread. If the there
	 * is no session registered, a new one is created and returned.
	 * 
	 * @return
	 */
	@NonNull
	Session getCurrentSession();

	/**
	 * Sets the given session for the current thread.
	 * 
	 * @param session
	 */
	void setCurrentSession(Session session);

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
	 * Executes the given {@link Callable} in another thread setting the using the
	 * session of the given id. This is useful to gain different privileges.
	 * 
	 * @param sessionId
	 * @param callable
	 * @return
	 */
	<T> T executeInSessionContext(String sessionId, Callable<T> callable);

	/**
	 * Executes the given {@link Callable} in another thread using the system (=
	 * root) session context.
	 * 
	 * @param callable
	 * @return
	 */
	<T> T executeInSystemSessionContext(Callable<T> callable);

}
