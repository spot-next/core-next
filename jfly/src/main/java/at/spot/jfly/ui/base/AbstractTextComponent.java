package at.spot.jfly.ui.base;

import j2html.tags.ContainerTag;

public abstract class AbstractTextComponent extends AbstractComponent {
	private String text;

	public AbstractTextComponent(final String tagName, final String text) {
		super(tagName);
		text(text);
	}

	public AbstractTextComponent(final String text) {
		this("span", text);
	}

	public <C extends AbstractTextComponent> C text(final String text) {
		this.text = text;

		updateClientComponent();

		return (C) this;
	}

	public String text() {
		return this.text;
	}

	@Override
	public ContainerTag build() {
		final ContainerTag raw = super.build();
		raw.withText(text);

		return raw;
	}
}
