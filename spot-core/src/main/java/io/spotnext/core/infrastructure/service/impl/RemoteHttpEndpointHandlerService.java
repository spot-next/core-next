package io.spotnext.core.infrastructure.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.exception.AuthenticationException;
import io.spotnext.core.infrastructure.http.DataResponse;
import io.spotnext.core.infrastructure.http.ExceptionResponse;
import io.spotnext.core.infrastructure.http.HttpResponse;
import io.spotnext.core.infrastructure.http.HttpStatus;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.I18nService;
import io.spotnext.core.infrastructure.service.SerializationService;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.UserService;
import io.spotnext.core.infrastructure.support.HttpRequestHolder;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.infrastructure.support.init.ModuleInit;
import io.spotnext.core.management.annotation.Handler;
import io.spotnext.core.management.annotation.RemoteEndpoint;
import io.spotnext.core.management.exception.RemoteServiceInitException;
import io.spotnext.core.management.support.AuthenticationFilter;
import io.spotnext.core.management.support.NoopAuthenticationFilter;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.security.service.AuthenticationService;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;
import io.spotnext.support.util.ClassUtil;
import spark.Filter;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Route;
import spark.Service;
import spark.route.HttpMethod;

/**
 * This HTTP service base class scans the implementing class for annotated method and register it as Spark request endpoints.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@org.springframework.stereotype.Service
@SuppressWarnings("PMD.TooManyStaticImports")
public class RemoteHttpEndpointHandlerService extends AbstractService {

	public static final String HTTP_HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public static final String HTTP_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public static final String HTTP_HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String HTTP_HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String HTTP_HEADER_ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	public static final String HTTP_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	public static final Class<? extends AuthenticationFilter> DEFAULT_AUTHENTICATION_HANDLER = NoopAuthenticationFilter.class;

	private boolean isStarted = false;

	@Autowired
	protected SerializationService serializationService;

	@Autowired
	protected I18nService i18nService;

	@Autowired
	protected AuthenticationService authenticationService;

	@Autowired
	protected SessionService sessionService;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected ResponseTransformer jsonResponseTransformer;

	@Value("${service.typesystem.rest.keystore.file:}")
	private String keystoreFilePath;

	@Value("${service.typesystem.rest.keystore.password:}")
	private String keystorePassword;

	protected Map<Integer, Service> services = new HashMap<>();

	/**
	 * key = port, value = endpoint instance
	 */
	final private Map<Integer, List<Object>> handlersRegistry = new HashMap<>();
	final Map<Class<ResponseTransformer>, ResponseTransformer> responseTransformers = new HashMap<>();
	final Map<Class<AuthenticationFilter>, AuthenticationFilter> authenticationFilters = new HashMap<>();

	/**
	 * Listens for {@link org.springframework.boot.context.event.ApplicationReadyEvent}s and scans the corresponding context for endpoints. If the context
	 * contains the startup {@link io.spotnext.infrastructure.support.init.ModuleInit} then the HTTP interfaces will be started up (cannot register new
	 * endpoints then).
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

			// wait for the all contexts to be started before starting the spark service
			// it's not possible to add new routes to already started spark instances
			if (ModuleInit.isBootComplete(event.getApplicationContext())) {
				init();
				isStarted = true;
			}
		} else {
			// TODO: maybe restart the service?
			Logger.debug(() -> "Ignoring context refresh event, as remote endpoints have already been started.");
		}
	}

	/**
	 * <p>
	 * init.
	 * </p>
	 *
	 * @throws io.spotnext.core.management.exception.RemoteServiceInitException if any.
	 */
	// @SuppressFBWarnings(value = { "REC_CATCH_EXCEPTION", "UI_INHERITANCE_UNSAFE_GETRESOURCE" })
	public void init() throws RemoteServiceInitException {
//		Security.addProvider(new OpenSSLProvider());
//		sslContextFactory.setProvider("Conscrypt");

		for (final Map.Entry<Integer, List<Object>> endpointEntry : handlersRegistry.entrySet()) {

			Service service;
			try {
				service = Service.ignite();
				service.staticFileLocation("/public");

				// setup CORS for the static files to allow access from all origins
				// the notFound router also contain CORS logic
				service.staticFiles.header(HTTP_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
				service.port(endpointEntry.getKey());

				if (StringUtils.isNotBlank(keystoreFilePath) && StringUtils.isNotBlank(keystorePassword)) {
					final String keystore = (!keystoreFilePath.startsWith("/") ? "/" : "") + keystoreFilePath;
					// use this "unsafe" access to allow to override the same keystore file used from other JAR files
					final String absoluteKeystoreFilePath = getClass().getResource(keystore).toExternalForm();
					service.secure(absoluteKeystoreFilePath, keystorePassword, null, null);
				}

				// register the service for later use
				services.put(endpointEntry.getKey(), service);
			} catch (final IllegalStateException e) {
				throw new RemoteServiceInitException(
						String.format("Could not start HTTP service on port %s", endpointEntry.getKey()), e);
			}

			for (final Object endpoint : endpointEntry.getValue()) {
				final RemoteEndpoint remoteEndpoint = ClassUtil.getAnnotation(endpoint.getClass(),
						RemoteEndpoint.class);

				Class<? extends Filter> classAutenticationFilterType = remoteEndpoint.authenticationFilter();
				for (final Method method : endpoint.getClass().getMethods()) {
					
					final Handler handler = ClassUtil.getAnnotation(method, Handler.class);

					if (handler != null) {
						ResponseTransformer transformer = null;
						final String mimeType = handler.mimeType().toString();

						// only override the class authentication filter, if it's not the default one
						var methodAuthenticationFilterType = classAutenticationFilterType;
						if (!DEFAULT_AUTHENTICATION_HANDLER.equals(handler.authenticationFilter())) {
							methodAuthenticationFilterType = handler.authenticationFilter();
						}

						var authenticationFilter = authenticationFilters.get(methodAuthenticationFilterType);
						// the authentication will not be handled in the
						// "before" handler to allow us to have different
						// authenticationFilters for each remote endpoint
						final Route route = new HttpRoute(endpoint, method,
								authenticationFilter, mimeType);

						Logger.debug(String.format("URL: %s, Method: %s, Filter: %s", remoteEndpoint.pathMapping()[0], method, authenticationFilter));
						
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

						final List<String> pathMappings = new ArrayList<>();

						for (String basePath : remoteEndpoint.pathMapping()) {
							for (String path : handler.pathMapping()) {
								pathMappings.add(StringUtils.join(basePath, path).replace("//", "/"));
							}
						}

						for (String pathMapping : pathMappings) {
							if (handler.method() == HttpMethod.trace) {
								service.trace(pathMapping, mimeType, route, transformer);
							}

							if (handler.method() == HttpMethod.connect) {
								service.connect(pathMapping, mimeType, route, transformer);
							}

							if (handler.method() == HttpMethod.options) {
								service.options(pathMapping, mimeType, route, transformer);
							}

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
			}

			try { // create routes for HTTP methods
				service.exception(Exception.class, (exception, request, response) -> {
					Logger.exception(exception.getMessage(), exception);
					cleanup();
					response.status(HttpStatus.INTERNAL_SERVER_ERROR.value());
				});

				service.before((request, response) -> {
					Logger.debug(() -> request.requestMethod() + " " + request.url() + " from " + request.ip() + ":"
							+ request.headers().stream().collect(Collectors.toMap(h -> h, h -> request.headers(h))));
					// store current request for the thread
					HttpRequestHolder.setRequest(request);

					// if authentication was successful we can setup a
					// session
					setupSession(service, request, response);
					setupLocale(request);
					setupEncoding(request, response);
					setupCorsHeaders(request, response);
				});

				// CORS is handled using a notFound router, as it doesn't seem to be possible to have a fallback "match-all" (/*) together with an more specific
				// OPTIONS route. If we'd use service.options("/*") we would lose the possibility to create OPTIONS controllers at all!
				service.notFound((request, response) -> {
					// if the http method is OPTIONS and the CORS header is present, this is most likely a CORS request
					if (org.eclipse.jetty.http.HttpMethod.OPTIONS.toString().matches(request.raw().getMethod())
							&& request.headers(HTTP_HEADER_ACCESS_CONTROL_REQUEST_HEADERS) != null) {

						// so we set the status to OK, otherwise this would be 404 and the CORS request would fail
						response.status(HttpStatus.OK.value());
					}

					// don't return any content and leave the status code 404 in other cases
					return null;
				});

				service.after((request, response) -> {
					cleanup();
//					setupEncoding(request, response);
//					response.raw().flushBuffer();
				});

				service.init();
			} catch (final Exception e) {
				throw new RemoteServiceInitException("Could not start remote endpoints", e);
			}
		}
	}

	protected void cleanup() {
		HttpRequestHolder.clear();
		persistenceService.evictCaches();
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
	protected void setupLocale(final Request request) {
		LocaleContextHolder.setLocale(request.raw().getLocale());
	}

	/**
	 * Checks if the client accepts compressed responses and sets the http response headers accordingly. This will make Spark/Jetty compress the output
	 * automatically.
	 * 
	 * @param request
	 * @param response
	 */
	protected void setupEncoding(final Request request, final Response response) {
		final String acceptEncoding = request.headers("Accept-Encoding");

		if (StringUtils.containsAny(acceptEncoding, "gzip")) {
			response.raw().addHeader("Content-Encoding", "gzip");
		}
	}

	protected void setupCorsHeaders(Request request, Response response) {
		// we mirror the desired results from the browser
		final String accessControlRequestHeaders = request.headers(HTTP_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
		if (accessControlRequestHeaders != null) {
			response.header(HTTP_HEADER_ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
		}

		final String accessControlRequestMethod = request.headers(HTTP_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
		if (accessControlRequestMethod != null) {
			response.header(HTTP_HEADER_ACCESS_CONTROL_ALLOW_METHODS, accessControlRequestMethod);
		}

		response.header(HTTP_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		response.header(HTTP_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
	}

	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
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

		// @SuppressFBWarnings("REC_CATCH_EXCEPTION")
		@Override
		public Object handle(final Request request, final Response response) throws Exception {
			response.type(contentType);

			Object ret = null;

			try {
				if (authenticationFilter != null) {
					authenticationFilter.handle(request, response);
				}

				ret = httpMethodImpl.invoke(serviceImpl, request, response);

			} catch (HaltException e) {
				// if this is thrown, the request will not be processed any further
				throw e;
			} catch (final Exception e) {
				if (!(e instanceof AuthenticationException)) {
					Logger.exception(StringUtils.defaultString(e.getMessage(), e.getClass().getName()), e);
				}

				Throwable realException = e;

				if (e instanceof InvocationTargetException) {
					final InvocationTargetException ie = (InvocationTargetException) e;
					realException = ie.getTargetException() != null ? ie.getTargetException() : ie;
				}

				// wrap the exception and forward it to the response transformer
				// there it will be handled appropriately
				if (realException instanceof AuthenticationException) {
					// return the authentication response or a generic exception response
					ret = ((AuthenticationException) realException).getResponse()
							.orElse(ExceptionResponse.withStatus(HttpStatus.UNAUTHORIZED, (Exception) realException));
				} else if (realException instanceof Exception) {
					ret = ExceptionResponse.internalServerError((Exception) realException);
				} else {
					throw new IllegalStateException("Cannot handle exception of type 'Throwable'.");
				}

				cleanup();
			}

			return processResponse(response, ret);
		}

		/**
		 * If the givn response body object is of type {@link DataResponse} the http status code is set according to {@link DataResponse#getStatusCode()}. Also
		 * the actual payload is returned, not the wrapper object itself. In any other case the given response body object is returned.
		 * 
		 * @param response
		 * @param responseBody
		 */
		protected Object processResponse(final Response response, final Object responseBody) {
			if (!response.raw().isCommitted()) {
				if (responseBody instanceof HttpResponse) {
					final HttpResponse body = (HttpResponse) responseBody;
					response.status(body.getHttpStatus().value());
					return body;
				}

				return responseBody;
			} else {
				return null;
			}
		}
	}

	/**
	 * <p>
	 * registerHandler.
	 * </p>
	 *
	 * @param port     a int.
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
