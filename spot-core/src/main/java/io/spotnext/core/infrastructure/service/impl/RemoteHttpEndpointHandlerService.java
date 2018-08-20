package io.spotnext.core.infrastructure.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.SerializationException;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.http.Payload;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.http.Status;
import io.spotnext.core.infrastructure.service.I18nService;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.support.MimeType;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.exception.RemoteServiceInitException;
import io.spotnext.core.management.support.HttpAuthorizationType;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.core.support.util.ClassUtil;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Route;
import spark.Service;
import spark.route.HttpMethod;

/**
 * This HTTP service base class scans the implementing class for annotated
 * method and register it as Spark request endpoints.
 */
@org.springframework.stereotype.Service
@SuppressWarnings("PMD.TooManyStaticImports")
public class RemoteHttpEndpointHandlerService extends AbstractService {

	@Resource
	protected SerializationService serializationService;

	@Resource
	protected I18nService i18nService;

	@Resource
	protected AuthenticationService authenticationService;

	@Resource
	protected SessionService sessionService;

	@Resource
	protected UserService<User, UserGroup> userService;

	@Resource
	protected ResponseTransformer jsonResponseTransformer;

	protected Service service;

	/**
	 * key = port, value = endpoint instance
	 */
	final private Map<Integer, List<Object>> handlersRegistry = new HashMap<>();

	@PostConstruct
	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	public void init() throws RemoteServiceInitException {
		for (final Object endpoint : getApplicationContext().getBeansWithAnnotation(RemoteEndpoint.class).values()) {
			final RemoteEndpoint remoteEndpoint = ClassUtil.getAnnotation(endpoint.getClass(), RemoteEndpoint.class);

			if (remoteEndpoint != null) {
				int port = remoteEndpoint.port();

				if (StringUtils.isNotBlank(remoteEndpoint.portConfigKey())) {
					port = configurationService.getInteger(remoteEndpoint.portConfigKey(), port);
				}

				registerHandler(port, endpoint);
			}
		}

		for (final Map.Entry<Integer, List<Object>> endpointEntry : handlersRegistry.entrySet()) {

			try {
				service = Service.ignite();
				service.port(endpointEntry.getKey());
			} catch (final IllegalStateException e) {
				loggingService.warn(e.getMessage());
			}

			for (final Object endpoint : endpointEntry.getValue()) {
				final RemoteEndpoint remoteEndpoint = ClassUtil.getAnnotation(endpoint.getClass(),
						RemoteEndpoint.class);

				for (final Method method : endpoint.getClass().getMethods()) {
					final Handler handler = ClassUtil.getAnnotation(method, Handler.class);

					if (handler != null) {
						ResponseTransformer transformer = null;
						final String mimeType = handler.mimeType().toString();
						final Route route = new HttpRoute(endpoint, method, mimeType);

						if (handler.responseTransformer() != null) {
							transformer = Registry.getApplicationContext().getBean(handler.responseTransformer());
						} else {
							transformer = jsonResponseTransformer;
						}

						String pathMappingEndpoint = remoteEndpoint.pathMapping();
						final String pathMappingHandler = handler.pathMapping();
						if (!pathMappingEndpoint.endsWith("/") && !pathMappingHandler.startsWith("/")) {
							pathMappingEndpoint += "/";
						}

						final String pathMapping = StringUtils.join(pathMappingEndpoint, pathMappingHandler);

						if (handler.method() == HttpMethod.get) {
							service.get(pathMapping, mimeType, route, transformer);
						}

						if (handler.method() == HttpMethod.post) {
							service.post(pathMapping, mimeType, route, transformer);
						}

						if (handler.method() == HttpMethod.put) {
							service.put(pathMapping, mimeType, route, transformer);
						}

						if (handler.method() == HttpMethod.delete) {
							service.delete(pathMapping, mimeType, route, transformer);
						}

						if (handler.method() == HttpMethod.head) {
							service.head(pathMapping, mimeType, route, transformer);
						}

						if (handler.method() == HttpMethod.patch) {
							service.patch(pathMapping, mimeType, route, transformer);
						}

						// handler of last resort, eg for 404 error pages
						// get("*", MimeType.JAVASCRIPT.toString(), (request,
						// response) -> {
						// return RequestStatus.notFound().error("The requested
						// URL
						// is not available.");
						// }, new JsonResponseTransformer());
					}
				}

				try { // create routes for HTTP methods

					service.exception(Exception.class, (exception, request, response) -> {
						loggingService.exception(exception.getMessage(), exception);

						final Payload empty = Payload.empty();
						empty.addError(new Status("internal.error", exception.getMessage()));

						final HttpResponse<?> status = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
						status.setBody(empty);

						try {
							response.body(serializationService.toJson(status));
						} catch (final SerializationException e) {
							response.body("Cannot serialize error response body");
						}
					});

					service.before((request, response) -> {
						setupSession(request, response);
						setupLocale();
					});

					service.notFound((request, response) -> {
						final Payload ret = Payload.empty();
						ret.addError(new Status("not.found", ""));

						final HttpResponse<?> status = new HttpResponse(HttpStatus.NOT_FOUND);
						status.setBody(ret);
						response.type(MimeType.JSON.toString());
						return jsonResponseTransformer.render(ret);
					});

					// after((request, response) -> {
					// response.header("Content-Encoding", "gzip");
					// });

					service.init();

				} catch (final Exception e) {
					loggingService.exception(String.format(
							"Cannot start HTTP service (%s), there is already an instance running on that port",
							getBeanName()), e);
				}
			}
		}
	}

	/**
	 * Sets up sessions and handles user authentication.
	 * 
	 * @param request
	 * @param response
	 */
	protected void setupSession(final Request request, final Response response) {
		// check if the web session has already a reference to the
		// backend session
		String spotSessionId = request.session().attribute("spotSessionId");

		Session spotSession = null;

		// if yes then we fetch the backend session
		if (StringUtils.isNotBlank(spotSessionId)) {
			spotSession = sessionService.getSession(spotSessionId);
		}

		// if it is null we create a new one
		if (spotSession == null) {
			spotSession = sessionService.createSession(true);
			spotSessionId = spotSession.getId();

			// and store the session id in the web session
			request.session().attribute("spotSessionId", spotSessionId);
		}

		if (userService.isCurrentUserAnonymous()) {
			// authenticate
			final User authenticatedUser = authenticate(request, response);

			if (authenticatedUser != null) {
				userService.setCurrentUser(authenticatedUser);
			} else {
				response.header("WWW-Authenticate", HttpAuthorizationType.BASIC.toString());
				service.halt(401);
			}
		}
	}

	/**
	 * Uses the {@link AuthenticationService} to authenticate a user using a basic
	 * authentication request header fields.
	 * 
	 * @param request
	 * @param response
	 */
	protected User authenticate(final Request request, final Response response) {
		final String encodedHeader = StringUtils.trim(
				StringUtils.substringAfter(request.headers("Authorization"), HttpAuthorizationType.BASIC.toString()));

		User authenticatedUser = null;

		if (StringUtils.isNotBlank(encodedHeader)) {
			final String decodedHeader = new String(Base64.getDecoder().decode(encodedHeader), StandardCharsets.UTF_8);

			final String[] credentials = StringUtils.split(decodedHeader, ":", 2);

			if (credentials != null && credentials.length == 2) {
				/*
				 * the http authentication password is encoded in MD5, by default we are also
				 * using the MD5 password strategy, so we simply set {@link
				 * AuthenticationService#isEncrypted} to true
				 */
				authenticatedUser = authenticationService.getAuthenticatedUser(credentials[0], credentials[1], true);
			}
		}

		return authenticatedUser;
	}

	/**
	 * Set the default locale in every new request thread that is being created
	 */
	protected void setupLocale() {
		LocaleContextHolder.setLocale(i18nService.getDefaultLocale());
	}

	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
	 *
	 */
	protected static class HttpRoute implements Route {
		private final Object serviceImpl;
		private final Method httpMethodImpl;
		private final String contentType;

		public HttpRoute(final Object serviceImpl, final Method httpMethodImpl) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
			this.contentType = "text/html";
		}

		public HttpRoute(final Object serviceImpl, final Method httpMethodImpl, final String contentType) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
			this.contentType = contentType;
		}

		@Override
		public Object handle(final Request request, final Response response) throws Exception {
			response.type(contentType);

			Object ret = null;

			try {
				ret = httpMethodImpl.invoke(serviceImpl, request, response);
			} catch (final InvocationTargetException e) {
				final HttpResponse<?> errorResponse = HttpResponse.internalError();

				final String message = e.getTargetException() != null ? e.getTargetException().getMessage()
						: e.getMessage();
				errorResponse.getBody().addError(new Status("error.internal", message));
				ret = errorResponse;
			}

			return processResponse(response, ret);
		}

		/**
		 * If the givn response body object is of type {@link HttpResponse} the http
		 * status code is set according to {@link HttpResponse#getStatusCode()}. Also
		 * the actual payload is returned, not the wrapper object itself. In any other
		 * case the given response body object is returned.
		 * 
		 * @param response
		 * @param responseBody
		 */
		protected Object processResponse(final Response response, final Object responseBody) {
			if (responseBody instanceof HttpResponse) {
				final HttpResponse<?> body = (HttpResponse<?>) responseBody;
				response.status(body.getStatusCodeValue());
				return body.getBody();
			}

			return responseBody;
		}
	}

	// protected static class RequestStatus {
	// private int httpStatus;
	// private Object payload;
	// private final List<String> warnings = new ArrayList<>();
	// private final List<String> errors = new ArrayList<>();
	//
	// public RequestStatus(final int httpStatus) {
	// this.httpStatus = httpStatus;
	// }
	//
	// public RequestStatus warn(final String warning) {
	// warnings.add(warning);
	//
	// return this;
	// }
	//
	// public RequestStatus error(final String error) {
	// errors.add(error);
	//
	// return this;
	// }
	//
	// public int httpStatus() {
	// return this.httpStatus;
	// }
	//
	// public Object payload() {
	// return payload;
	// }
	//
	// public RequestStatus payload(final Object payload) {
	// this.payload = payload;
	//
	// return this;
	// }
	//
	// public List<String> warnings() {
	// return warnings;
	// }
	//
	// public List<String> errors() {
	// return errors;
	// }
	//
	// public RequestStatus httpStatus(final int httpStatus) {
	// this.httpStatus = httpStatus;
	//
	// return this;
	// }
	//
	// public static final RequestStatus success() {
	// return new RequestStatus(HttpStatus.OK_200);
	// };
	//
	// public static final RequestStatus notFound() {
	// return new RequestStatus(HttpStatus.NOT_FOUND_404);
	// };
	//
	// public static final RequestStatus serverError() {
	// return new RequestStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
	// };
	// }

	public void registerHandler(final int port, final Object endpoint) {
		List<Object> handlers = this.handlersRegistry.get(port);
		if (handlers == null) {
			handlers = new ArrayList<>();
			this.handlersRegistry.put(port, handlers);
		}

		handlers.add(endpoint);
	}

}
