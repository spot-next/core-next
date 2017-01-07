package at.spot.jfly;

import static j2html.TagCreator.html;

public abstract class JFlyApplication {

	final private Head head;
	final private Body body;

	public JFlyApplication() {
		head = createHeader();
		body = createBody();
	}

	protected abstract Head createHeader();

	protected abstract Body createBody();

	/**
	 * Returns the head element of the HTML page.
	 * 
	 * @return
	 */
	public Head head() {
		return head;
	}

	/**
	 * Returns the body element of the HTML page.
	 * 
	 * @return
	 */
	public Body body() {
		return body;
	}

	public String render() {
		return html().with(head().build()).with(body().build()).render();
	}

}
