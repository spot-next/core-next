package at.spot.jfly;

import j2html.tags.ContainerTag;

public class Stylesheet extends AbstractComponent {

	private String location;

	public Stylesheet() {
		super("link");
	}

	public Stylesheet(final String styleLocation) {
		this();
		location(styleLocation);
	}

	public String location() {
		return location;
	}

	public Stylesheet location(final String location) {
		this.location = location;

		return this;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();

		raw.withRel("stylesheet").withType("text/css");
		raw.withHref(location());

		return raw;
	}
}
