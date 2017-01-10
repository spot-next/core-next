package at.spot.jfly.ui.base;

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
