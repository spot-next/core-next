package at.spot.core.management.service.impl;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.head;
import static spark.Spark.post;
import static spark.Spark.put;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.SerializationService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.management.annotation.Delete;
import at.spot.core.management.annotation.Get;
import at.spot.core.management.annotation.Head;
import at.spot.core.management.annotation.Post;
import at.spot.core.management.annotation.Put;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.service.RemoteInterfaceServiceEndpoint;
import at.spot.core.support.util.ClassUtil;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

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
				final Get get = ClassUtil.getAnnotation(m, Get.class);
				final Post post = ClassUtil.getAnnotation(m, Post.class);
				final Put put = ClassUtil.getAnnotation(m, Put.class);
				final Delete delete = ClassUtil.getAnnotation(m, Delete.class);
				final Head head = ClassUtil.getAnnotation(m, Head.class);

				if (get != null) {
					final Route route = new HttpRoute(this, m);
					get(get.pathMapping(), get.mimeType(), route, get.responseTransformer().newInstance());
				}

				if (post != null) {
					final Route route = new HttpRoute(this, m);
					post(post.pathMapping(), post.mimeType(), route, post.responseTransformer().newInstance());
				}

				if (put != null) {
					final Route route = new HttpRoute(this, m);
					put(put.pathMapping(), put.mimeType(), route, put.responseTransformer().newInstance());
				}

				if (delete != null) {
					final Route route = new HttpRoute(this, m);
					delete(delete.pathMapping(), delete.mimeType(), route, delete.responseTransformer().newInstance());
				}

				if (head != null) {
					final Route route = new HttpRoute(this, m);
					head(head.pathMapping(), head.mimeType(), route, head.responseTransformer().newInstance());
				}
			}

			exception(Exception.class, (exception, request, response) -> {
				loggingService.exception(exception.getMessage(), exception);

				final HttpMethodStatus status = new HttpMethodStatus(false);
				status.error(exception.getMessage());
				status.success(false);

				response.status(500);
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

		public HttpRoute(final AbstractHttpServiceEndpoint serviceImpl, final Method httpMethodImpl) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
		}

		@Override
		public Object handle(final Request request, final Response response) throws Exception {
			return httpMethodImpl.invoke(serviceImpl, request, response);
		}
	}

	protected static class HttpMethodStatus {
		private boolean success = true;
		private final List<String> warnings = new ArrayList<>();
		private final List<String> errors = new ArrayList<>();

		public HttpMethodStatus() {
		}

		public HttpMethodStatus(final boolean success) {
			this.success = success;
		}

		public void success(final boolean success) {
			this.success = success;
		}

		public void warn(final String warning) {
			warnings.add(warning);
		}

		public void error(final String error) {
			errors.add(error);
		}
	}
}
