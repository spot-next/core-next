package io.spotnext.core.infrastructure.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.SessionService;

/**
 * <p>
 * DefaultSessionService class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultSessionService implements SessionService {

	protected Map<String, Session> sessions = new ConcurrentHashMap<>();
	protected ThreadLocal<Session> currentSession = new ThreadLocal<>();

	/** {@inheritDoc} */
	@Override
	public Session createSession(final boolean registerAsCurrentSession) {
		// if there's already a session for this thread, we remove it
		if (currentSession.get() != null) {
			sessions.remove(currentSession.get().getId());
		}

		// generate a new unique id as session id
		final UUID sessionId = UUID.randomUUID();

		// create the session object
		final Session session = new Session(sessionId.toString());

		if (registerAsCurrentSession) {
			// set the newly created session as current session for the current
			// thread
			setCurrentSession(session);
		}

		// add the session to the session storage.
		sessions.put(session.getId(), session);

		return session;
	}

	/** {@inheritDoc} */
	@Override
	public Session getCurrentSession() {
		Session session = currentSession.get();

		if (session == null) {
			session = createSession(true);
		}

		return session;
	}

	/** {@inheritDoc} */
	@Override
	public void setCurrentSession(final Session session) {
		currentSession.set(session);
	}

	/** {@inheritDoc} */
	@Override
	public Session getSession(final String sessionId) {
		return sessions.get(sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public void closeSession(final String sessionID) {
		final Session session = sessions.get(sessionID);

		if (session != null) {
			session.invalidate();
		}

		sessions.remove(sessionID);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T executeInSessionContext(final String sessionId, final Callable<T> callable) {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public <T> T executeInSystemSessionContext(final Callable<T> callable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Session> getAllSessions() {
		return Collections.unmodifiableMap(sessions);
	}

}
