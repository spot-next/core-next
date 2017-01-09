package at.spot.jfly.ui;

import at.spot.jfly.AbstractComponent;
import at.spot.jfly.Component;
import at.spot.jfly.style.ComponentStyle;
import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class NavBar extends AbstractContainerComponent {

	AbstractComponent header = null;

	public NavBar() {
		super("nav");
		addStyle(ComponentStyle.NavBar);
	}

	public AbstractComponent header() {
		return this.header;
	}

	public NavBar header(AbstractComponent header) {
		this.header = header;
		return this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		return raw;
	}

	@Override
	protected void buildChildren(ContainerTag raw) {
		final ContainerTag container = TagCreator.div().withClass("container-fluid");
		raw.with(container);

		final ContainerTag navHeader = TagCreator.div().withClass(ComponentStyle.NavBarHeader.toString());
		final ContainerTag childContainer = TagCreator.ul().withClass(ComponentStyle.NavBarContent.toString());

		container.with(navHeader);
		container.with(childContainer);

		if (header != null) {
			navHeader.with(header.build().withClass(ComponentStyle.NavBarBrand.toString()));
		}

		for (final Component c : children) {
			childContainer.with(TagCreator.li().with(c.build()));
		}
	}
}
