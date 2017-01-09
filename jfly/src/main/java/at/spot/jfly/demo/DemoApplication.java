package at.spot.jfly.demo;

import at.spot.jfly.Body;
import at.spot.jfly.ComponentController;
import at.spot.jfly.Head;
import at.spot.jfly.JFlyApplication;
import at.spot.jfly.event.JsEvent;
import at.spot.jfly.style.ButtonStyle;
import at.spot.jfly.style.LabelStyle;
import at.spot.jfly.style.NavbarStyle;
import at.spot.jfly.ui.Badge;
import at.spot.jfly.ui.Button;
import at.spot.jfly.ui.Label;
import at.spot.jfly.ui.LinkAction;
import at.spot.jfly.ui.NavBar;
import j2html.TagCreator;

public class DemoApplication extends JFlyApplication {

	@Override
	protected Head createHeader() {
		final Head head = new Head().title("Hello world");

		return head;
	}

	@Override
	protected Body createBody() {
		final NavBar navBar = new NavBar().addStyle(NavbarStyle.Inverse);

		navBar.header(new LinkAction("spOt"));
		navBar.addChild(new LinkAction("Settings").location("#settings"));
		navBar.addChild(new LinkAction("Logout").onEvent(JsEvent.click, (e) -> {
			ComponentController.instance().invokeFunctionCall("jfly", "reloadApp");
			ComponentController.instance().closeCurrentSession();
		}));

		final Body body = new Body().addChild(navBar);
		final Button button = new Button("Say hello!").addStyle(ButtonStyle.Success);

		body.addChild(button);
		body.addChild(new Label("test").addStyle(LabelStyle.Danger));
		body.addChild(new Badge("42"));

		button.onEvent(JsEvent.click, e -> {
			body.addChild(TagCreator.h1("hello world"));
		});

		button.onEvent(JsEvent.mouseover, e -> {
			button.text("over");
		});

		button.onEvent(JsEvent.mouseout, e -> {
			button.text("and out");
		});

		return body;
	}
}
