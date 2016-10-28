package at.spot.core.management.service.impl;

import static spark.Spark.get;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.management.annotation.Get;
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
public abstract class AbstractHttpServiceEndpoint implements RemoteInterfaceServiceEndpoint {

	@Autowired
	protected LoggingService loggingService;

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		Spark.port(getPort());

		// create routes for GET method
		try {
			for (final Method m : this.getClass().getMethods()) {
				Get annotations = ClassUtil.getAnnotation(m, Get.class);

				if (annotations != null) {
					Route route = new HttpRoute(this, m);
					get(annotations.pathMapping(), annotations.mimeType(), route,
							annotations.responseTransformer().newInstance());
				}
			}
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RemoteServiceInitException(e.getMessage(), e);
		}
	}

	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
	 *
	 */
	protected class HttpRoute implements Route {
		private AbstractHttpServiceEndpoint serviceImpl;
		private Method httpMethodImpl;

		public HttpRoute(AbstractHttpServiceEndpoint serviceImpl, Method httpMethodImpl) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
		}

		@Override
		public Object handle(Request request, Response response) throws Exception {
			return httpMethodImpl.invoke(serviceImpl, request, response);
		}
	}
}
