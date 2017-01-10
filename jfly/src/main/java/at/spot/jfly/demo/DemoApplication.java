package at.spot.jfly.demo;

import at.spot.jfly.ComponentController;
import at.spot.jfly.JFlyApplication;
import at.spot.jfly.event.JsEvent;
import at.spot.jfly.style.ButtonStyle;
import at.spot.jfly.style.LabelStyle;
import at.spot.jfly.style.NavbarStyle;
import at.spot.jfly.ui.action.Button;
import at.spot.jfly.ui.action.LinkAction;
import at.spot.jfly.ui.display.Badge;
import at.spot.jfly.ui.display.Label;
import at.spot.jfly.ui.html.Body;
import at.spot.jfly.ui.html.Head;
import at.spot.jfly.ui.navigation.NavBar;
import j2html.TagCreator;

public class DemoApplication extends JFlyApplication {

	@Override
	protected Head createHeader() {
		final Head head = new Head().title("Hello world");

		return head;
	}

	@Override
	protected Body createBody() {
		final NavBar navBar = new NavBar().addStyleClasses(NavbarStyle.Inverse.toString());

		navBar.header(new LinkAction("spOt"));
		navBar.addChildren(new LinkAction("Settings").location("#settings"));
		navBar.addChildren(new LinkAction("Logout").onEvent(JsEvent.click, (e) -> {
			ComponentController.instance().invokeFunctionCall("jfly", "reloadApp");
			ComponentController.instance().closeCurrentSession();
		}));

		final Body body = new Body().addChildren(navBar);
		final Button button = new Button("Say hello!").addStyleClasses(ButtonStyle.Success.toString());

		body.addChildren(button);
		body.addChildren(new Label("test").addStyleClasses(LabelStyle.Danger.toString()));
		body.addChildren(new Badge("42"));

		button.onEvent(JsEvent.click, e -> {
			button.text("over");
			body.addChildren(TagCreator.h1("hello world"));
		});

		button.onEvent(JsEvent.mouseout, e -> {
			button.text("and out");
		});

		return body;
	}
}
