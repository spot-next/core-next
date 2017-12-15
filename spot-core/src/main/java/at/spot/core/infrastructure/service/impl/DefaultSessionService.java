package at.spot.core.infrastructure.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.http.Session;
import at.spot.core.infrastructure.service.SessionService;

@Service
public class DefaultSessionService implements SessionService {

	Map<String, Session> sessions = new ConcurrentHashMap<>();
	ThreadLocal<Session> currentSession = new ThreadLocal<>();

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
			currentSession.set(session);
		}

		// add the session to the session storage.
		sessions.put(session.getId(), session);

		return session;
	}

	@Override
	public Session getCurrentSession() {
		Session session = currentSession.get();

		if (session == null) {
			session = createSession(true);
		}

		return session;
	}

	@Override
	public void setCurrentSession(final Session session) {
		currentSession.set(session);
	}

	@Override
	public Session getSession(final String sessionId) {
		return sessions.get(sessionId);
	}

	@Override
	public void closeSession(final String sessionID) {
		final Session session = sessions.get(sessionID);

		if (session != null) {
			session.invalidate();
		}

		sessions.remove(sessionID);
	}

	@Override
	public <T> T executeInSessionContext(final String sessionId, final Callable<T> callable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T executeInSystemSessionContext(final Callable<T> callable) {
		// TODO Auto-generated method stub
		return null;
	}

}
