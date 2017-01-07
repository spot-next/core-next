package at.spot.jfly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import at.spot.jfly.event.OnClickEvent;

@WebSocket
public class ComponentController {
	private static final ComponentController instance = new ComponentController();

	private final Map<String, Component> registeredComponents = new ConcurrentHashMap<>();

	private final ThreadLocal<Session> context = new ThreadLocal<>();

	// Store sessions if you want to, for example, broadcast a message to all
	// users
	private final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

	public static ComponentController instance() {
		return instance;
	}

	@OnWebSocketConnect
	public void connected(final Session session) {
		sessions.add(session);
	}

	@OnWebSocketClose
	public void closed(final Session session, final int statusCode, final String reason) {
		sessions.remove(session);
	}

	@OnWebSocketMessage
	public void message(final Session session, final String message) throws IOException {
		setCurrentSession(session);

		System.out.println("Got: " + message); // Print message
		// session.getRemote().sendString(message); // and send it back

		final JsonObject msg = new Gson().fromJson(message, JsonObject.class);

		final String componentUuid = msg.get("uuid").getAsString();

		if (StringUtils.isNotBlank(componentUuid)) {
			final Component component = registeredComponents.get(componentUuid);
			final String event = msg.get("event").getAsString();
			final JsonObject payload = msg.get("payload").getAsJsonObject();

			handleEvent(component, event, payload);
		}
	}

	protected void setCurrentSession(final Session session) {
		context.set(session);
	}

	protected Session getCurrentSession() {
		return context.get();
	}

	public void registerComponent(final Component component) {
		registeredComponents.put(component.uuid(), component);
	}

	protected void handleEvent(final Component component, final String event, final JsonObject payload) {
		switch (event) {
		case "click":
			component.handleEvent(new OnClickEvent(component));
			break;

		default:
			break;
		}
	}

	public void invoke(final Component component, final String method, final Object... parameters) {
		final Map<String, Object> content = new HashMap<>();
		content.put("uuid", component.uuid());
		content.put("method", method);
		content.put("methodParams", parameters);

		try {
			if (getCurrentSession() != null) {
				getCurrentSession().getRemote().sendString(new Gson().toJson(content));
			}
		} catch (final IOException e) {
			// throw new RemoteException("Cannot reach remote client", e);
			System.out.println("error");
		}
	}

}
