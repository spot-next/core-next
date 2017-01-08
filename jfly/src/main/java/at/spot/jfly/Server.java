package at.spot.jfly;

import at.spot.jfly.demo.DemoApplication;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

	private final int port;
	private final Class<? extends JFlyApplication> appClass;

	public Server(final Class<? extends JFlyApplication> appClass, final int port) {
		this.appClass = appClass;
		this.port = port;
	}

	public void init() {
		Spark.port(port);
		Spark.staticFileLocation("/web");
		Spark.webSocket("/com", ComponentController.instance());
		Spark.get("/", (req, res) -> render(req, res, appClass));
		Spark.exception(Exception.class, (ex, reg, res) -> ex.printStackTrace());
		Spark.init();
	}

	protected String render(final Request request, final Response response,
			final Class<? extends JFlyApplication> appClass) throws InstantiationException, IllegalAccessException {

		JFlyApplication app = request.session().attribute("application");

		if (app == null) {
			app = appClass.newInstance();

			request.session().attribute("application", app);
		}

		return app.render();
	}

	public static void main(final String[] args) {
		final Server server = new Server(DemoApplication.class, 8080);
		server.init();
	}
}
