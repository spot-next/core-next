package at.spot.core.management.service.impl;

import static spark.Spark.get;

import java.lang.reflect.Method;
import java.net.SocketException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.management.annotation.Get;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.management.service.RemoteInterfaceService;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * This HTTP service base class scans the implementing class for annotated method and register it as Spark request endpoints. 
 *
 */
public abstract class AbstractHttpService implements RemoteInterfaceService {

	private static final int DEFAULT_PORT = 9000;
	
	protected Integer port = null;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;
	
	@Log(logLevel=LogLevel.INFO, message="Initiating remote type system access service ...")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException, SocketException {
		if (this.port == null) {
			loggingService.warn("No port configured: using standard port " + DEFAULT_PORT);
			Spark.port(DEFAULT_PORT);
		} else {
			Spark.port(this.port);
		}

		// create routes for GET method
		try {
			for (final Method m : this.getClass().getMethods()) {
				Get annotations = typeService.getAnnotation(m, Get.class);
				
				if (annotations != null) {
					Route route = new HttpRoute(this, m);
					get(annotations.pathMapping(), annotations.mimeType(), route, annotations.responseTransformer().newInstance());
				}
			}
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RemoteServiceInitException(e.getMessage(), e);
		}
	}
	
	protected void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Helper class that implements a Spark {@link Route} to serve HTTP calls.
	 *
	 */
	protected class HttpRoute implements Route {
		private AbstractHttpService serviceImpl;
		private Method httpMethodImpl;

		public HttpRoute(AbstractHttpService serviceImpl, Method httpMethodImpl) {
			this.serviceImpl = serviceImpl;
			this.httpMethodImpl = httpMethodImpl;
		}
		
		@Override
		public Object handle(Request request, Response response) throws Exception {
			return httpMethodImpl.invoke(serviceImpl, request, response);
		}
	}
}
