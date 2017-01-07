package at.spot.jfly.ui;

import at.spot.jfly.AbstractComponent;
import j2html.tags.ContainerTag;

public class GenericComponent extends AbstractComponent {

	final ContainerTag raw;

	public GenericComponent(final ContainerTag tag) {
		super();
		this.raw = tag;
	}

	@Override
	public ContainerTag build() {
		return raw;
	}
}
