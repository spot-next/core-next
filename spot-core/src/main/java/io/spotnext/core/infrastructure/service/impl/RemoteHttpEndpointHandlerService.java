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
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.ExceptionResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.I18nService;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.support.HttpRequestHolder;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.exception.RemoteServiceInitException;
import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.core.persistence.service.PersistenceService;
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
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
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
	protected PersistenceService persistenceService;

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
	 * Listens for {@link org.springframework.boot.context.event.ApplicationReadyEvent}s and scans the corresponding
	 * context for endpoints. If the context contains the startup {@link io.spotnext.core.infrastructure.support.init.ModuleInit}
	 * then the HTTP interfaces will be started up (cannot register new endpoints
	 * then).
	 *
	 * @param event that signals that the context has been started
	 * @throws io.spotnext.core.management.exception.RemoteServiceInitException
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
	 * Check if the given context contains the startup {@link io.spotnext.core.infrastructure.support.init.ModuleInit}.
	 *
	 * @param context the spring context that has been started/refreshed
	 * @return true if the context contains the startup {@link io.spotnext.core.infrastructure.support.init.ModuleInit}
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

	/**
	 * <p>init.</p>
	 *
	 * @throws io.spotnext.core.management.exception.RemoteServiceInitException if any.
	 */
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

				Class<? extends Filter> autenticationFilterType = remoteEndpoint.authenticationFilter();

				for (final Method method : endpoint.getClass().getMethods()) {
					final Handler handler = ClassUtil.getAnnotation(method, Handler.class);

					if (handler != null) {
						ResponseTransformer transformer = null;
						final String mimeType = handler.mimeType().toString();

						if (handler.authenticationFilter() != null) {
							autenticationFilterType = handler.authenticationFilter();
						}

						// the authentication will not be handled in the
						// "before" handler to allow us to have different
						// authenticationFilters for each remote endpoint
						final Route route = new HttpRoute(endpoint, method,
								authenticationFilters.get(autenticationFilterType), mimeType);

						if (handler.responseTransformer() != null) {
							transformer = responseTransformers.get(handler.responseTransformer());

							if (transformer == null) {
								throw new IllegalStateException(
										String.format("Could not initialize response transformer for class %s",
												handler.responseTransformer()));
							}
						} else {
							// use the JSON default transformer
							transformer = jsonResponseTransformer;
						}

						final String pathMapping = StringUtils.join(remoteEndpoint.pathMapping(), handler.pathMapping()).replace("//", "/");

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
			}

			try { // create routes for HTTP methods

				service.exception(Exception.class, (exception, request, response) -> {
					loggingService.exception(exception.getMessage(), exception);
					cleanupOnException(exception);
//					Spark.halt(HttpStatus.INTERNAL_SERVER_ERROR.value());
				});

				service.before((request, response) -> {
					// store current request for the thread
					HttpRequestHolder.setRequest(request);

					// if authentication was successful we can setup a
					// session
					setupSession(service, request, response);
					setupLocale(request);
				});

//					service.notFound((request, response) -> {
//						Spark.halt(HttpStatus.NOT_FOUND.value());
//						response.status();
//						return null;
//					});

				service.after((request, response) -> {
					HttpRequestHolder.clear();

					setupEncoding(request, response);
				});

				service.init();

			} catch (final Exception e) {
				throw new RemoteServiceInitException("Could not start remote endpoints", e);
			}
		}
	}

	protected void cleanupOnException(Exception exception) {
		loggingService.exception(exception.getMessage(), exception);
		persistenceService.unbindSession();
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

		sessionService.setCurrentSession(spotSession);
	}

	/**
	 * Set the default locale in every new request thread that is being created
	 */
	protected void setupLocale(Request request) {
		LocaleContextHolder.setLocale(request.raw().getLocale());
	}

	/**
	 * Checks if the client accepts compressed responses and sets the http response
	 * headers accordingly. This will make Spark/Jetty compress the output
	 * automatically.
	 * 
	 * @param request
	 * @param response
	 */
	protected void setupEncoding(Request request, Response response) {
		String acceptEncoding = request.headers("Accept-Encoding");

		if (StringUtils.containsAny(acceptEncoding, "gzip")) {
			response.header("Content-Encoding", "gzip");
		}
	}

	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
	 *
	 */
	protected class HttpRoute implements Route {
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
			} catch (final Exception e) {
				// wrap the exception and forward it to the response transformer
				// there it will be handled appropriately

				if (e instanceof InvocationTargetException) {
					final InvocationTargetException ie = (InvocationTargetException) e;
					final Throwable inner = ie.getTargetException() != null ? ie.getTargetException() : ie;

					if (inner instanceof Exception) {
						ret = ExceptionResponse.internalServerError((Exception) inner);
					} else {
						throw new IllegalStateException("Cannot handle exception of type 'Throwable'.");
					}

					cleanupOnException(ie);
				}
			}

			return processResponse(response, ret);
		}

		/**
		 * If the givn response body object is of type {@link DataResponse} the http
		 * status code is set according to {@link DataResponse#getStatusCode()}. Also
		 * the actual payload is returned, not the wrapper object itself. In any other
		 * case the given response body object is returned.
		 * 
		 * @param response
		 * @param responseBody
		 */
		protected Object processResponse(final Response response, final Object responseBody) {
			if (responseBody instanceof HttpResponse) {
				final HttpResponse body = (HttpResponse) responseBody;
				response.status(body.getHttpStatus().value());
				return body;
			}

			return responseBody;
		}
	}

	/**
	 * <p>registerHandler.</p>
	 *
	 * @param port a int.
	 * @param endpoint a {@link java.lang.Object} object.
	 */
	public void registerHandler(final int port, final Object endpoint) {
		List<Object> handlers = this.handlersRegistry.get(port);
		if (handlers == null) {
			handlers = new ArrayList<>();
			this.handlersRegistry.put(port, handlers);
		}

		handlers.add(endpoint);
	}

}
