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

import com.google.gson.JsonObject;

import at.spot.jfly.event.Event;
import at.spot.jfly.event.JsEvent;
import at.spot.jfly.ui.base.AbstractComponent;
import at.spot.jfly.ui.base.Component;
import at.spot.jfly.util.GsonUtil;

@WebSocket
public class ComponentController {
	private static final ComponentController instance = new ComponentController();

	private final Map<String, Component> registeredComponents = new ConcurrentHashMap<>();
	private final ThreadLocal<Session> context = new ThreadLocal<>();
	private final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

	public static ComponentController instance() {
		return instance;
	}

	@OnWebSocketConnect
	public void connected(final Session session) {
		addSession(session);
		setCurrentSession(session);
	}

	@OnWebSocketClose
	public void closed(final Session session, final int statusCode, final String reason) {
		closeSession(session);
	}

	@OnWebSocketMessage
	public void message(final Session session, final String message) throws IOException {
		System.out.println("Got: " + message); // Print message

		setCurrentSession(session);

		if (StringUtils.isNotBlank(message)) {
			final JsonObject msg = GsonUtil.fromJson(message, JsonObject.class);

			// if this is an initial request, we return the current component
			// states
			if (msg.get("messageType") != null
					&& StringUtils.equalsIgnoreCase(msg.get("messageType").getAsString(), "hello")) {
				sendInitialComponentStates(session);
			} else { // this is a regular message, most likely an event
				final String componentUuid = msg.get("uuid").getAsString();

				if (StringUtils.isNotBlank(componentUuid)) {
					final Component component = registeredComponents.get(componentUuid);
					final String event = msg.get("event").getAsString();
					final Map<String, Object> payload = GsonUtil.fromJson(msg.get("payload"), Map.class);

					handleEvent(component, event, payload);
				}
			}
		}
	}

	protected void addSession(final Session session) {
		sessions.add(session);
	}

	protected void closeSession(final Session session) {
		if (session != null) {
			try {
				session.disconnect();
			} catch (final IOException e) {
				e.printStackTrace();
			}

			session.close();
			sessions.remove(session);
		}
	}

	public void closeCurrentSession() {
		closeSession(getCurrentSession());
	}

	protected void setCurrentSession(final Session session) {
		context.set(session);
	}

	public boolean isCalledInRequest() {
		return getCurrentSession() != null;
	}

	protected Session getCurrentSession() {
		return context.get();
	}

	public Map<String, Component> getRegisteredComponents() {
		return registeredComponents;
	}

	public void registerComponent(final Component component) {
		registeredComponents.put(component.uuid(), component);
	}

	protected void handleEvent(final Component component, final String event, final Map<String, Object> payload) {
		final JsEvent e = JsEvent.valueOf(event);
		component.handleEvent(new Event(e, component, payload));
	}

	protected void sendInitialComponentStates(final Session session) throws IOException {
		final Map<String, Object> message = new HashMap<>();

		message.put("type", "componentInitialization");
		message.put("componentStates", registeredComponents);

		sendMessage(message, session);
	}

	/**
	 * Calls a javascript function an passes the given parameters.
	 * 
	 * @param method
	 * @param parameters
	 */
	public void invokeFunctionCall(final String object, final String functionCall, final Object... parameters) {
		final Map<String, Object> message = new HashMap<>();

		message.put("type", "functionCall");
		message.put("object", object);
		message.put("func", functionCall);
		message.put("params", parameters);

		sendMessage(message);
	}

	/**
	 * Calls a javascript function on the given object with the given
	 * parameters.
	 * 
	 * @param component
	 * @param method
	 * @param parameters
	 */
	public void invokeComponentManipulation(final Component component, final String method,
			final Object... parameters) {

		final Map<String, Object> message = new HashMap<>();

		message.put("type", "objectManipulation");
		message.put("componentUuid", component.uuid());
		message.put("method", method);
		message.put("params", parameters);

		sendMessage(message);
	}

	public void sendMessage(final Map<String, Object> message, final Session session) throws IOException {
		session.getRemote().sendString(GsonUtil.toJson(message));
	}

	public void sendMessage(final Map<String, Object> message) {
		try {
			if (getCurrentSession() != null) {
				sendMessage(message, getCurrentSession());
			}
		} catch (final IOException e) {
			// throw new RemoteException("Cannot reach remote client", e);
			System.out.println("error");
		}
	}

	public void updateComponentData(final Component component) {
		final String data = component.toJson();

		final Map<String, Object> message = new HashMap<>();

		message.put("type", "componentUpdate");
		message.put("componentUuid", component.uuid());
		message.put("componentState", component);

		sendMessage(message);
	}

	public void redrawComponentData(final AbstractComponent component) {
		invokeFunctionCall("jfly", "replaceComponent", component.uuid(), component.build().render());
	}

}
