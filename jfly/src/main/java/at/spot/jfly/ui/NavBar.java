package at.spot.jfly.ui;

import at.spot.jfly.AbstractComponent;
import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class NavBar extends AbstractComponent {

	public NavBar() {
		super("nav");
	}

	@Override
	public ContainerTag build() {
		ContainerTag raw = TagCreator.nav();

		raw.withClass("navbar navbar-default");
		// raw.attr("data-toggle", "collapse");
		// raw.attr("role", "navigation");

		ContainerTag container = TagCreator.div();
		container.withClass("navbar-header");

		raw.with(container);

		// collapse navbar-collapse navbar-collapse-1

		return raw;
	}
}
