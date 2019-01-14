package io.spotnext.core.management.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.constant.CoreConstants;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.impl.DefaultUserService;
import io.spotnext.core.infrastructure.support.LogLevel;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.support.BasicAuthenticationFilter;
import io.spotnext.core.management.transformer.JsonResponseTransformer;
import io.spotnext.core.persistence.TypeSystemAction;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.itemtype.core.beans.UserData;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

/**
 * The /model REST endpoint.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@RemoteEndpoint(portConfigKey = "service.typesystem.rest.port", port = 19000, pathMapping = "/v1/system/", authenticationFilter = BasicAuthenticationFilter.class)
public class SystemEndpoint extends AbstractRestEndpoint {

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected SessionService sessionService;

	/**
	 * Provides type system actions to initialize, update and clear the database schema
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = { "/typesystem/:action" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse executeTypeSystemAction(final Request request, final Response response) {
		String typeSystemAction = request.params("action");

		final Optional<TypeSystemAction> action = TypeSystemAction.forValue(typeSystemAction);

		if (action.isPresent()) {
			switch (action.get()) {
			case INIT:
				persistenceService.initializeTypeSystem();
				break;
			case UPDATE:
				persistenceService.updateTypeSystem();
				break;
			case VALIDATE:
				persistenceService.validateTypeSystem();
				break;
			case CLEAR:
				break;
			}

			return DataResponse.ok();
		}

		return DataResponse.badRequest().withError("error.typesystem.action.unknown", "No valid type system action supplied");
	}

	/**
	 * Returns all active sessions.
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.get, pathMapping = { "/sessions" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse listAllSessions(final Request request, final Response response) {

		final Map<String, Session> sessions = sessionService.getAllSessions();
		final List<SessionData> sessionData = sessions.values().stream().map(v -> new SessionData(v))
				.collect(Collectors.toList());

		return DataResponse.ok().withPayload(sessionData);
	}

	/**
	 * Returns all active sessions.
	 */
	@Log(logLevel = LogLevel.DEBUG, measureExecutionTime = true)
	@Handler(method = HttpMethod.delete, pathMapping = {
			"/sessions/:sessionId" }, mimeType = MimeType.JSON, responseTransformer = JsonResponseTransformer.class)
	public HttpResponse terminateSession(final Request request, final Response response) {
		String sessionIdToKill = request.params("sessionId");

		boolean success = false;

		if (StringUtils.isNotBlank(sessionIdToKill)) {
			final Session session = sessionService.getSession(sessionIdToKill);

			if (session != null) {
				sessionService.closeSession(sessionIdToKill);
				success = true;
			}
		}

		if (success) {
			return DataResponse.ok();
		}

		return DataResponse.badRequest().withError("error.sessions.id.invalid", "Could not terminate session - invalid sessionId.");
	}

	protected class SessionData {
		private String id;
		private String userName;
		private Date creationData;

		public SessionData(Session session) {
			this.id = session.getId();
			this.userName = Optional.ofNullable((UserData) session.getAttribute(CoreConstants.SESSION_KEY_CURRENT_USER))
					.map(u -> u.getUid()) //
					.orElse(DefaultUserService.DEFAULT_USER.getUid());
			this.creationData = new Date(session.getCreationTime());
		}
	}
}
