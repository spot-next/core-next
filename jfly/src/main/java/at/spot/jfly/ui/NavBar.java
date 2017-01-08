package at.spot.jfly.ui;

import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class NavBar extends AbstractContainerComponent {

	public NavBar() {
		super("nav");
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		raw.withClass("navbar navbar-default");
		// raw.attr("data-toggle", "collapse");
		// raw.attr("role", "navigation");

		final ContainerTag container = TagCreator.div();
		container.withClass("navbar-header");

		raw.with(container);

		// collapse navbar-collapse navbar-collapse-1

		return raw;
	}
}
