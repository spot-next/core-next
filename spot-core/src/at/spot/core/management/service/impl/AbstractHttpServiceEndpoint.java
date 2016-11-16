package at.spot.core.management.service.impl;

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

import javax.annotation.PostConstruct;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

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

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		try {
			Spark.port(getPort());
		} catch (final Exception e) {
			loggingService.warn(e.getMessage());
		}

		try {
			// create routes for HTTP methods

			for (final Method m : this.getClass().getMethods()) {
				final Handler handler = ClassUtil.getAnnotation(m, Handler.class);

				ResponseTransformer transformer = Registry.getApplicationContext()
						.getBean(handler.responseTransformer());

				if (handler != null && handler.method() == HttpMethod.get) {
					final Route route = new HttpRoute(this, m, handler.mimeType());
					get(handler.pathMapping(), handler.mimeType(), route, transformer);
				}

				if (handler != null && handler.method() == HttpMethod.post) {
					final Route route = new HttpRoute(this, m, handler.mimeType());
					post(handler.pathMapping(), handler.mimeType(), route, transformer);
				}

				if (handler != null && handler.method() == HttpMethod.put) {
					final Route route = new HttpRoute(this, m, handler.mimeType());
					put(handler.pathMapping(), handler.mimeType(), route, transformer);
				}

				if (handler != null && handler.method() == HttpMethod.delete) {
					final Route route = new HttpRoute(this, m, handler.mimeType());
					delete(handler.pathMapping(), handler.mimeType(), route, transformer);
				}

				if (handler != null && handler.method() == HttpMethod.head) {
					final Route route = new HttpRoute(this, m, handler.mimeType());
					head(handler.pathMapping(), handler.mimeType(), route, transformer);
				}

				if (handler != null && handler.method() == HttpMethod.patch) {
					final Route route = new HttpRoute(this, m, handler.mimeType());
					patch(handler.pathMapping(), handler.mimeType(), route, transformer);
				}
			}

			exception(Exception.class, (exception, request, response) -> {
				loggingService.exception(exception.getMessage(), exception);

				final RequestStatus status = RequestStatus.serverError().error(exception.getMessage());

				response.status(status.httpStatus());
				response.body(serializationService.toJson(status));
			});

			// after((request, response) -> {
			// response.header("Content-Encoding", "gzip");
			// });

		} catch (final Exception e) {
			loggingService.warn(
					String.format("Cannot start HTTP service (%s), there is already an instance running on that port",
							getBeanName()));
		}
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
