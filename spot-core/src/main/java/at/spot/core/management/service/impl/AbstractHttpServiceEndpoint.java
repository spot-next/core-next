package at.spot.core.management.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import at.spot.core.infrastructure.exception.SerializationException;
import at.spot.core.infrastructure.http.HttpResponse;
import at.spot.core.infrastructure.http.HttpStatus;
import at.spot.core.infrastructure.http.Payload;
import at.spot.core.infrastructure.http.Session;
import at.spot.core.infrastructure.http.Status;
import at.spot.core.infrastructure.service.I18nService;
import at.spot.core.infrastructure.service.SerializationService;
import at.spot.core.infrastructure.service.SessionService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.management.annotation.Handler;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.service.RemoteInterfaceServiceEndpoint;
import at.spot.core.management.support.HttpAuthorizationType;
import at.spot.core.security.service.AuthenticationService;
import at.spot.core.support.util.ClassUtil;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class AbstractHttpServiceEndpoint extends AbstractService implements RemoteInterfaceServiceEndpoint {

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

	protected Service service;

	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		try {
			service = Service.ignite();
			service.port(getPort());
		} catch (final IllegalStateException e) {
			loggingService.warn(e.getMessage());
		}

		try { // create routes for HTTP methods
			for (final Method m : this.getClass().getMethods()) {
				final Handler handler = ClassUtil.getAnnotation(m, Handler.class);

				if (handler != null) {
					ResponseTransformer transformer = null;
					final String mimeType = handler.mimeType().toString();
					final Route route = new HttpRoute(this, m, mimeType);

					if (handler.responseTransformer() != null) {
						transformer = Registry.getApplicationContext().getBean(handler.responseTransformer());
					}

					if (handler.method() == HttpMethod.get) {
						service.get(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.post) {
						service.post(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.put) {
						service.put(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.delete) {
						service.delete(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.head) {
						service.head(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.patch) {
						service.patch(handler.pathMapping(), mimeType, route, transformer);
					}

					// handler of last resort, eg for 404 error pages
					// get("*", MimeType.JAVASCRIPT.toString(), (request,
					// response) -> {
					// return RequestStatus.notFound().error("The requested URL
					// is not available.");
					// }, new JsonResponseTransformer());
				}
			}

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
				return ret;
			});

			// after((request, response) -> {
			// response.header("Content-Encoding", "gzip");
			// });

			service.init();

		} catch (final Exception e) {
			loggingService.exception(
					String.format("Cannot start HTTP service (%s), there is already an instance running on that port",
							getBeanName()),
					e);
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
	 * @return
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

	@Override
	@PreDestroy
	public void shutdown() {
		service.stop();
	}

	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
	 *
	 */
	protected static class HttpRoute implements Route {
		private final AbstractHttpServiceEndpoint serviceImpl;
		private final Method httpMethodImpl;
		private final String contentType;

		public HttpRoute(final AbstractHttpServiceEndpoint serviceImpl, final Method httpMethodImpl) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
			this.contentType = "text/html";
		}

		public HttpRoute(final AbstractHttpServiceEndpoint serviceImpl, final Method httpMethodImpl,
				final String contentType) {
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

				String message = e.getTargetException() != null ? e.getTargetException().getMessage() : e.getMessage();
				errorResponse.getBody().addError(new Status("error.internal", message));
				ret = errorResponse;
				serviceImpl.loggingService.exception("An error occured during execution of request", e);
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
		 * @return
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

}
