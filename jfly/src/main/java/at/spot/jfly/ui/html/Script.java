package at.spot.jfly.ui.html;

import at.spot.jfly.ui.base.AbstractComponent;
import j2html.tags.ContainerTag;

public class Script extends AbstractComponent {

	private String location;

	public Script() {
		super("script");
	}

	public Script(final String scriptLocation) {
		this();
		this.location(scriptLocation);
	}

	public String location() {
		return location;
	}

	public Script location(final String location) {
		this.location = location;

		return this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		raw.withType("text/javascript");
		raw.withSrc(location());

		return raw;
	}
}