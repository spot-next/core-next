package at.spot.jfly.ui.navigation;

import at.spot.jfly.layout.Alignment;
import at.spot.jfly.style.ComponentType;
import at.spot.jfly.style.NavbarStyle;
import at.spot.jfly.ui.base.AbstractContainerComponent;
import at.spot.jfly.ui.base.AbstractTextComponent;
import at.spot.jfly.ui.base.Component;
import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class NavBar extends AbstractContainerComponent {

	private Alignment placement = Alignment.Top;
	private AbstractTextComponent header = null;

	public NavBar() {
		super("nav");
		componentType(ComponentType.NavBarDefault);
	}

	public AbstractTextComponent header() {
		return this.header;
	}

	public NavBar header(final AbstractTextComponent header) {
		this.header = header;
		this.header.addStyleClasses(NavbarStyle.NavBarHeaderBrand);
		redrawClientComponent();
		return this;
	}

	public Alignment placement() {
		return this.placement;
	}

	public NavBar placement(final Alignment placement) {
		this.placement = placement;
		redrawClientComponent();
		return this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build().withClass(componentType().internalName());
		return raw;
	}

	@Override
	protected void buildChildren(final ContainerTag raw) {
		final ContainerTag container = TagCreator.div().withClass("container-fluid");
		raw.with(container);

		final ContainerTag navHeader = TagCreator.div().withClass("navbar-header");
		final ContainerTag childContainer = TagCreator.div().withClass("collapse navbar-collapse");
		final ContainerTag childContainerList = TagCreator.ul().withClass("nav navbar-nav");

		container.with(navHeader);
		container.with(childContainer);
		childContainer.with(childContainerList);

		if (header != null) {
			navHeader.with(header.build().withClass("navbar-brand"));
		}

		for (final Component c : children) {
			childContainerList.with(TagCreator.li().with(c.build()));
		}
	}
}
