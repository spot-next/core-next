package io.spotnext.core.infrastructure.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
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
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.exception.RemoteServiceInitException;
import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.core.support.util.ClassUtil;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import spark.Filter;
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

	private boolean isStarted = false;

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

	protected Map<Integer, Service> services = new HashMap<>();

	/**
	 * key = port, value = endpoint instance
	 */
	final private Map<Integer, List<Object>> handlersRegistry = new HashMap<>();
	final Map<Class<ResponseTransformer>, ResponseTransformer> responseTransformers = new HashMap<>();
	final Map<Class<AuthenticationFilter>, AuthenticationFilter> authenticationFilters = new HashMap<>();

	/**
	 * Listens for {@link ApplicationReadyEvent}s and scans the corresponding
	 * context for endpoints. If the context contains the startup {@link ModuleInit}
	 * then the HTTP interfaces will be started up (cannot register new endpoints
	 * then).
	 * 
	 * @param event that signals that the context has been started
	 * @throws RemoteServiceInitException
	 */
	@EventListener(classes = ApplicationReadyEvent.class)
	public void onApplicationReady(final ApplicationReadyEvent event) throws RemoteServiceInitException {
		// TODO: refactor this
		if (!isStarted) {
			// scan context for remote endpoints
			for (final Object endpoint : event.getApplicationContext().getBeansWithAnnotation(RemoteEndpoint.class)
					.values()) {
				final RemoteEndpoint remoteEndpoint = ClassUtil.getAnnotation(endpoint.getClass(),
						RemoteEndpoint.class);

				if (remoteEndpoint != null) {
					int port = remoteEndpoint.port();

					if (StringUtils.isNotBlank(remoteEndpoint.portConfigKey())) {
						port = configurationService.getInteger(remoteEndpoint.portConfigKey(), port);
					}

					registerHandler(port, endpoint);
				}
			}

			// register all response transformers
			event.getApplicationContext().getBeansOfType(ResponseTransformer.class).forEach(
					(beanName, bean) -> responseTransformers.put((Class<ResponseTransformer>) bean.getClass(), bean));

			// register all authentication fitleres
			event.getApplicationContext().getBeansOfType(AuthenticationFilter.class).forEach(
					(beanName, bean) -> authenticationFilters.put((Class<AuthenticationFilter>) bean.getClass(), bean));

			// wait for the all contexts to be started before starting the spark
			// service
			// it's not possible to add new routes to already started spark
			// instances
			if (isBootComplete(event.getApplicationContext())) {
				init();
				isStarted = true;
			}
		} else {
			// TODO: maybe restart the service?
			loggingService.debug("Ignoring context refresh event, as remote endpoints have already been started.");
		}
	}

	/**
	 * Check if the given context contains the startup {@link ModuleInit}.
	 * 
	 * @param context the spring context that has been started/refreshed
	 * @return true if the context contains the startup {@link ModuleInit}
	 */
	public boolean isBootComplete(final ApplicationContext context) {
		try {
			context.getBean(Registry.getMainClass());

			// we don't care if the ModuleInit has finished initializing (like
			// import sample
			// data). But if this line is reached, the most inner child context
			// containing
			// the startup ModuleInit has been loaded. Therefore all beans have
			// already been scanned for remote endpoints.
			return true;
		} catch (final BeansException e) {
			return false;
		}
	}

	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	public void init() throws RemoteServiceInitException {
		for (final Map.Entry<Integer, List<Object>> endpointEntry : handlersRegistry.entrySet()) {

			Service service;
			try {
				service = Service.ignite();
				service.staticFileLocation("/public");
				service.port(endpointEntry.getKey());
				// register the service for later use
				services.put(endpointEntry.getKey(), service);
			} catch (final IllegalStateException e) {
				throw new RemoteServiceInitException(
						String.format("Could not start HTTP service on port %s", endpointEntry.getKey()), e);
			}

			for (final Object endpoint : endpointEntry.getValue()) {
				final RemoteEndpoint remoteEndpoint = ClassUtil.getAnnotation(endpoint.getClass(),
						RemoteEndpoint.class);

				final Class<? extends Filter> autenticationFilterType = remoteEndpoint.authenticationFilter();

				for (final Method method : endpoint.getClass().getMethods()) {
					final Handler handler = ClassUtil.getAnnotation(method, Handler.class);

					if (handler != null) {
						ResponseTransformer transformer = null;
						final String mimeType = handler.mimeType().toString();

						// the authentication will not be handled in the
						// "before" handler to allow us to have different
						// authenticationFilters for each remote endpoint
						final Route route = new HttpRoute(endpoint, method,
								authenticationFilters.get(autenticationFilterType), mimeType);

						if (handler.responseTransformer() != null) {
							transformer = responseTransformers.get(handler.responseTransformer());
						} else {
							// use the JSON default transformer
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
						// if authentication was successful we can setup a
						// session
						setupSession(service, request, response);
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
					throw new RemoteServiceInitException("Could not start remote endpoints", e);
				}
			}
		}
	}

	protected void authenticate(final Class<? extends Filter> autenticationFilterType, final Request request,
			final Response response) throws Exception {
		authenticationFilters.get(autenticationFilterType).handle(request, response);
	}

	/**
	 * Sets up sessions and handles user authentication.
	 * 
	 * @param request
	 * @param response
	 */
	protected void setupSession(final Service service, final Request request, final Response response) {
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
		private final AuthenticationFilter authenticationFilter;

		public HttpRoute(final Object serviceImpl, final Method httpMethodImpl,
				final AuthenticationFilter authenticationFilter) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
			this.contentType = "text/html";
			this.authenticationFilter = authenticationFilter;
		}

		public HttpRoute(final Object serviceImpl, final Method httpMethodImpl,
				final AuthenticationFilter authenticationFilter, final String contentType) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
			this.contentType = contentType;
			this.authenticationFilter = authenticationFilter;
		}

		@Override
		public Object handle(final Request request, final Response response) throws Exception {
			if (authenticationFilter != null) {
				authenticationFilter.handle(request, response);
			}

			response.type(contentType);

			Object ret = null;

			try {
				ret = httpMethodImpl.invoke(serviceImpl, request, response);
			} catch (final Throwable e) {
				final HttpResponse<?> errorResponse = HttpResponse.internalError();

				final String message;

				if (e instanceof InvocationTargetException) {
					InvocationTargetException ie = (InvocationTargetException) e;
					message = ie.getTargetException() != null ? ie.getTargetException().getMessage() : e.getMessage();
				} else {
					message = e.getMessage();
				}

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

	public void registerHandler(final int port, final Object endpoint) {
		List<Object> handlers = this.handlersRegistry.get(port);
		if (handlers == null) {
			handlers = new ArrayList<>();
			this.handlersRegistry.put(port, handlers);
		}

		handlers.add(endpoint);
	}

}
