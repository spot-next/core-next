package at.spot.core.management.service.impl;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.head;
import static spark.Spark.patch;
import static spark.Spark.post;
import static spark.Spark.put;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import at.spot.core.infrastructure.service.I18nService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SerializationService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.spring.support.Registry;
import at.spot.core.management.annotation.Handler;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.service.RemoteInterfaceServiceEndpoint;
import at.spot.core.support.util.ClassUtil;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Route;
import spark.Spark;
import spark.route.HttpMethod;

/**
 * This HTTP service base class scans the implementing class for annotated
 * method and register it as Spark request endpoints.
 *
 */
public abstract class AbstractHttpServiceEndpoint extends AbstractService implements RemoteInterfaceServiceEndpoint {

	@Autowired
	protected SerializationService serializationService;

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected I18nService i18nService;

	protected Locale defaultLocale = i18nService.getDefaultLocale();

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		try {
			Spark.port(getPort());
		} catch (final Exception e) {
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
						get(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.post) {
						post(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.put) {
						put(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.delete) {
						delete(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.head) {
						head(handler.pathMapping(), mimeType, route, transformer);
					}

					if (handler.method() == HttpMethod.patch) {
						patch(handler.pathMapping(), mimeType, route, transformer);
					}

					// handler of last resort, eg for 404 error pages
					// get("*", MimeType.JAVASCRIPT.toString(), (request,
					// response) -> {
					// return RequestStatus.notFound().error("The requested URL
					// is not available.");
					// }, new JsonResponseTransformer());
				}
			}

			exception(Exception.class, (exception, request, response) -> {
				loggingService.exception(exception.getMessage(), exception);

				final RequestStatus status = RequestStatus.serverError().error(exception.getMessage());

				response.status(status.httpStatus());
				response.body(serializationService.toJson(status));
			});

			before((request, response) -> {
				// check permissions

				// set the default locale in every new request thread that is
				// being created
				LocaleContextHolder.setLocale(defaultLocale);
			});

			// after((request, response) -> {
			// response.header("Content-Encoding", "gzip");
			// });

		} catch (final Exception e) {
			loggingService.exception(
					String.format("Cannot start HTTP service (%s), there is already an instance running on that port",
							getBeanName()),
					e);
		}
	}

	@Override
	@PreDestroy
	public void shutdown() {
		Spark.stop();
	}

	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
	 *
	 */
	protected class HttpRoute implements Route {
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
			return httpMethodImpl.invoke(serviceImpl, request, response);
		}
	}

	protected static class RequestStatus {
		private int httpStatus;
		private Object payload;
		private final List<String> warnings = new ArrayList<>();
		private final List<String> errors = new ArrayList<>();

		public RequestStatus(final int httpStatus) {
			this.httpStatus = httpStatus;
		}

		public RequestStatus warn(final String warning) {
			warnings.add(warning);

			return this;
		}

		public RequestStatus error(final String error) {
			errors.add(error);

			return this;
		}

		public int httpStatus() {
			return this.httpStatus;
		}

		public Object payload() {
			return payload;
		}

		public RequestStatus payload(final Object payload) {
			this.payload = payload;

			return this;
		}

		public List<String> warnings() {
			return warnings;
		}

		public List<String> errors() {
			return errors;
		}

		public RequestStatus httpStatus(final int httpStatus) {
			this.httpStatus = httpStatus;

			return this;
		}

		public static final RequestStatus success() {
			return new RequestStatus(HttpStatus.OK_200);
		};

		public static final RequestStatus notFound() {
			return new RequestStatus(HttpStatus.NOT_FOUND_404);
		};

		public static final RequestStatus serverError() {
			return new RequestStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		};
	}
}
