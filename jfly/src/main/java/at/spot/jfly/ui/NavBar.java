package at.spot.jfly.ui;

import at.spot.jfly.Component;
import at.spot.jfly.layout.Alignment;
import at.spot.jfly.style.ComponentType;
import at.spot.jfly.style.NavbarStyle;
import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class NavBar extends AbstractContainerComponent {

	private Alignment placement = Alignment.Top;
	private AbstractTextComponent header = null;

	public NavBar() {
		super("navbar");
		componentType(ComponentType.NavBarInverse);
		addStyleClasses(NavbarStyle.Default.toString());
	}

	public AbstractTextComponent header() {
		return this.header;
	}

	public NavBar header(final AbstractTextComponent header) {
		this.header = header;
		return this;
	}

	public Alignment placement() {
		return this.placement;
	}

	public NavBar placement(final Alignment placement) {
		this.placement = placement;
		return this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build().attr("placement", placement().toString());

		return raw;
	}

	@Override
	protected void buildChildren(final ContainerTag raw) {

		if (header != null) {
			raw.with(header.build().withClass(NavbarStyle.NavBarBrand.toString()).attr("slot", "brand"));
		}

		for (final Component c : children) {
			if (c instanceof AbstractTextComponent) {
				raw.with(TagCreator.li().with(c.build()));
			} else {
				raw.with(c.build());
			}
		}
	}
}
